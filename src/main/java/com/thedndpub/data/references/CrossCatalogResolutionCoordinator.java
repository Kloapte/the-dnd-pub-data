package com.thedndpub.data.references;

import com.thedndpub.data.services.BackgroundCatalog;
import com.thedndpub.data.services.ClassDomainSnapshot;
import com.thedndpub.data.services.CultCatalog;
import com.thedndpub.data.services.DeityCatalog;
import com.thedndpub.data.services.TreasureTableCatalog;
import com.thedndpub.data.services.BoonCatalog;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.LanguageCatalog;
import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.ObjectCatalog;
import com.thedndpub.data.services.BastionFacilityCatalog;
import com.thedndpub.data.services.EncounterCatalog;
import com.thedndpub.data.services.RaceCatalog;
import com.thedndpub.data.services.SkillCatalog;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.RewardCatalog;
import com.thedndpub.data.services.TrapCatalog;
import com.thedndpub.data.services.HazardCatalog;
import com.thedndpub.data.services.VehicleCatalog;
import com.thedndpub.data.services.VehicleUpgradeCatalog;
import com.thedndpub.data.services.VariantRuleCatalog;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Resolves the selected authored cross-catalog references once after target catalogs are available. */
@Component
public final class CrossCatalogResolutionCoordinator {
    private static final Pattern INDEX = Pattern.compile("\\[\\d+\\]");

    private final ResolutionSnapshot snapshot;

    public CrossCatalogResolutionCoordinator(
            SpellCatalog spells,
            ItemCatalog items,
            BackgroundCatalog backgrounds,
            ClassDomainSnapshot classes,
            SourceRegistry sourceRegistry,
            FeatCatalog feats,
            OptionalFeatureCatalog optionalFeatures,
            SkillCatalog skills,
            LanguageCatalog languages,
            RaceCatalog races,
            CultCatalog cults,
            BoonCatalog boons,
            ObjectCatalog objects,
            BastionFacilityCatalog facilities,
            EncounterCatalog encounters,
            RewardCatalog rewards,
            TrapCatalog traps,
            HazardCatalog hazards,
            VehicleCatalog vehicles,
            VehicleUpgradeCatalog vehicleUpgrades,
            VariantRuleCatalog variantRules,
            DeityCatalog deities,
            TreasureTableCatalog treasureTables) {
        AdapterRegistry adapters = new AdapterRegistry(List.of(
                new SpellTargetAdapter(spells, sourceRegistry),
                new ItemTargetAdapter(items, sourceRegistry, ReferenceTargetKind.ITEM),
                new ItemTargetAdapter(items, sourceRegistry, ReferenceTargetKind.BASE_ITEM),
                new FeatTargetAdapter(feats, sourceRegistry),
                new OptionalFeatureTargetAdapter(optionalFeatures, sourceRegistry),
                new SkillTargetAdapter(skills, sourceRegistry),
                new LanguageTargetAdapter(languages, sourceRegistry)));
        List<AuthoredReference> authored = new SourceAuthoredReferenceExtractor(
                classes, backgrounds, items, feats, optionalFeatures, races, cults, boons, objects, facilities,
                encounters, rewards, traps, hazards, vehicles, vehicleUpgrades, variantRules, deities,
                treasureTables).extract();
        ReferenceResolver resolver = new ReferenceResolver(adapters, sourceRegistry);
        List<ResolutionResult> results = authored.stream().map(resolver::resolve)
                .sorted(Comparator.comparing((ResolutionResult value) -> value.reference().owner().catalog())
                        .thenComparing(value -> value.reference().owner().stableId())
                        .thenComparing(value -> value.reference().fieldLocator())
                        .thenComparing(value -> value.reference().originalToken()))
                .toList();
        validateRequiredExactReferences(results);
        snapshot = buildSnapshot(results);
    }

    public ResolutionSnapshot snapshot() {
        return snapshot;
    }

    private static void validateRequiredExactReferences(List<ResolutionResult> results) {
        for (ResolutionResult result : results) {
            AuthoredReference reference = result.reference();
            if (reference.parsedSource() == null || reference.semanticForm() != ReferenceSemanticForm.DEFINITION) {
                continue;
            }
            boolean required = reference.targetKind() == ReferenceTargetKind.BASE_ITEM
                    || reference.owner().catalog().equals("class")
                    && reference.targetKind() == ReferenceTargetKind.ITEM
                    || (reference.owner().catalog().equals("class")
                    || reference.owner().catalog().equals("subclass"))
                    && reference.targetKind() == ReferenceTargetKind.SPELL;
            if (required && result.outcome() != ResolutionOutcome.RESOLVED) {
                throw new IllegalStateException("Required exact reference did not resolve: "
                        + reference.owner().stableId() + " " + reference.fieldLocator() + " "
                        + reference.originalToken() + " => " + result.outcome());
            }
        }
    }

    private static ResolutionSnapshot buildSnapshot(List<ResolutionResult> results) {
        Map<ResolutionOutcome, Long> byOutcome = new EnumMap<>(ResolutionOutcome.class);
        Map<ReferenceTargetKind, Long> byKind = new EnumMap<>(ReferenceTargetKind.class);
        results.forEach(result -> {
            byOutcome.merge(result.outcome(), 1L, Long::sum);
            byKind.merge(result.reference().targetKind(), 1L, Long::sum);
        });

        List<ResolutionDiagnostic> diagnostics = new ArrayList<>();
        for (ResolutionResult result : results) {
            DiagnosticSeverity severity = switch (result.outcome()) {
                case RESOLVED -> DiagnosticSeverity.INFO;
                case DEFERRED -> DiagnosticSeverity.INFO;
                case AMBIGUOUS, UNRESOLVED -> DiagnosticSeverity.WARNING;
            };
            diagnostics.add(ResolutionDiagnostic.from(result, severity));
        }

        Map<GroupKey, Long> aggregate = results.stream().collect(Collectors.groupingBy(
                result -> new GroupKey(
                        result.reference().owner().catalog(),
                        normalizeLocator(result.reference().fieldLocator()),
                        result.reference().targetKind(),
                        result.reference().semanticForm(),
                        result.outcome(),
                        result.candidateCardinality()),
                TreeMap::new,
                Collectors.counting()));
        List<ReferenceResolutionGroup> groups = aggregate.entrySet().stream()
                .map(entry -> new ReferenceResolutionGroup(entry.getKey().ownerCatalog(), entry.getKey().fieldLocator(),
                        entry.getKey().targetKind(), entry.getKey().semanticForm(), entry.getKey().outcome(),
                        entry.getKey().cardinality(), entry.getValue()))
                .toList();

        return new ResolutionSnapshot(results, diagnostics, byOutcome, byKind, groups);
    }

    private static String normalizeLocator(String locator) {
        return INDEX.matcher(locator).replaceAll("[]");
    }

    private record GroupKey(
            String ownerCatalog,
            String fieldLocator,
            ReferenceTargetKind targetKind,
            ReferenceSemanticForm semanticForm,
            ResolutionOutcome outcome,
            CandidateCardinality cardinality) implements Comparable<GroupKey> {
        @Override
        public int compareTo(GroupKey other) {
            int result = ownerCatalog.compareTo(other.ownerCatalog);
            if (result == 0) {
                result = fieldLocator.compareTo(other.fieldLocator);
            }
            if (result == 0) {
                result = targetKind.compareTo(other.targetKind);
            }
            if (result == 0) {
                result = semanticForm.compareTo(other.semanticForm);
            }
            if (result == 0) {
                result = outcome.compareTo(other.outcome);
            }
            if (result == 0) {
                result = cardinality.compareTo(other.cardinality);
            }
            return result;
        }
    }
}
