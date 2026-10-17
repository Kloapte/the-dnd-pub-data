package com.thedndpub.data.references;

import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.SkillCatalog;
import com.thedndpub.data.services.LanguageCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CrossCatalogResolutionTests {
    @Autowired
    private CrossCatalogResolutionCoordinator coordinator;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Autowired
    private SpellCatalog spellCatalog;

    @Autowired
    private FeatCatalog featCatalog;

    @Autowired
    private OptionalFeatureCatalog optionalFeatureCatalog;

    @Autowired
    private SkillCatalog skillCatalog;

    @Autowired
    private LanguageCatalog languageCatalog;

    @Test
    void corpusSnapshotMatchesQualifiedAmbiguousDeferredAndMissingReferenceEvidence() {
        ResolutionSnapshot snapshot = coordinator.snapshot();

        assertEquals(446, count(snapshot, "spell", ResolutionOutcome.RESOLVED,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.DEFINITION));
        assertEquals(1068, count(snapshot, "spellcasting", ResolutionOutcome.RESOLVED,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.DEFINITION)
                + count(snapshot, "spellcasting", ResolutionOutcome.AMBIGUOUS,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.DEFINITION)
                + count(snapshot, "spellcasting", ResolutionOutcome.DEFERRED,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.DEFINITION));
        assertEquals(620, count(snapshot, "spell", ResolutionOutcome.AMBIGUOUS,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.DEFINITION));
        assertEquals(2, count(snapshot, "spell", ResolutionOutcome.DEFERRED,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.DEFINITION));
        assertEquals(3, count(snapshot, "spell", ResolutionOutcome.DEFERRED,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.CHOICE));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.CHOICE)
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL)
                .allMatch(result -> result.outcome() == ResolutionOutcome.DEFERRED
                        && result.candidateCardinality() == CandidateCardinality.ONE
                        && result.reference().parsedSource() != null
                        && result.resolvedTargetId() == null));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS)
                .allMatch(result -> result.candidateCardinality() == CandidateCardinality.MANY
                        && result.candidates().size() > 1));
        assertEquals(186, count(snapshot, "spellcasting", ResolutionOutcome.DEFERRED,
                ReferenceTargetKind.SPELL, "class|subclass", ReferenceSemanticForm.FILTER));
        assertTrue(snapshot.results().stream()
                .filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.FILTER)
                .allMatch(result -> result.outcome() == ResolutionOutcome.DEFERRED
                        && result.candidates().isEmpty()));
        assertEquals(718, count(snapshot, "item", ResolutionOutcome.RESOLVED,
                ReferenceTargetKind.ITEM, "background", ReferenceSemanticForm.DEFINITION));
        assertEquals(8, count(snapshot, "item", ResolutionOutcome.UNRESOLVED,
                ReferenceTargetKind.ITEM, "background", ReferenceSemanticForm.DEFINITION));
        assertEquals(3, count(snapshot, "startingOptions.equipment", ResolutionOutcome.DEFERRED,
                ReferenceTargetKind.ITEM_GROUP, "class", ReferenceSemanticForm.GROUP));
        assertEquals(165, count(snapshot, "startingOptions.equipment", ResolutionOutcome.RESOLVED,
                ReferenceTargetKind.ITEM, "class", ReferenceSemanticForm.DEFINITION));
        assertTrue(snapshot.results().stream().anyMatch(result -> result.outcome() == ResolutionOutcome.RESOLVED
                && result.reference().originalToken().equals("studded leather armor|xphb")
                && result.resolvedDefinitionKind() == ReferenceTargetKind.BASE_ITEM));
        assertEquals(241, count(snapshot, "baseItem", ResolutionOutcome.RESOLVED,
                ReferenceTargetKind.BASE_ITEM, "item", ReferenceSemanticForm.DEFINITION));
        assertEquals(1570, countB1Outcome(snapshot, ResolutionOutcome.RESOLVED));
        assertEquals(620, countB1Outcome(snapshot, ResolutionOutcome.AMBIGUOUS));
        assertEquals(8, countB1Outcome(snapshot, ResolutionOutcome.UNRESOLVED));
        assertEquals(194, countB1Outcome(snapshot, ResolutionOutcome.DEFERRED));
        assertEquals(snapshot.results().size(), snapshot.diagnostics().size());
        assertEquals(snapshot.results().size(), snapshot.countsByOwnerFieldKindAndOutcome().stream()
                .mapToLong(ReferenceResolutionGroup::occurrences).sum());
    }

    private static long countB1Outcome(ResolutionSnapshot snapshot, ResolutionOutcome outcome) {
        return snapshot.results().stream()
                .filter(result -> result.outcome() == outcome)
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL
                        || result.reference().targetKind() == ReferenceTargetKind.ITEM
                        || result.reference().targetKind() == ReferenceTargetKind.BASE_ITEM
                        || result.reference().targetKind() == ReferenceTargetKind.ITEM_GROUP)
                .filter(result -> !result.reference().owner().catalog().equals("cult")
                        && !result.reference().owner().catalog().equals("boon")
                        && !result.reference().owner().catalog().equals("object")
                        && !result.reference().owner().catalog().equals("facility")
                        && !result.reference().owner().catalog().equals("encounter")
                        && !result.reference().owner().catalog().equals("reward")
                        && !result.reference().owner().catalog().equals("trap")
                        && !result.reference().owner().catalog().equals("hazard"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle-upgrade"))
                .filter(result -> !result.reference().owner().catalog().equals("variant-rule"))
                .filter(result -> !result.reference().owner().catalog().equals("deity"))
                .filter(result -> !result.reference().owner().catalog().equals("treasure-table"))
                .count();
    }

    @Test
    void knownUnqualifiedBrandingSmiteCandidatesRemainDeferred() {
        List<ResolutionResult> results = coordinator.snapshot().results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SPELL)
                .filter(result -> "branding smite".equalsIgnoreCase(result.reference().parsedName()))
                .filter(result -> result.reference().parsedSource() == null)
                .toList();

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(result -> result.outcome() == ResolutionOutcome.DEFERRED));
        assertTrue(results.stream().allMatch(result -> result.candidateCardinality() == CandidateCardinality.ONE));
        assertTrue(results.stream().allMatch(result -> result.resolvedTargetId() == null));
    }

    @Test
    void phbAndXphbAreSeparateExactSpellTargets() {
        var adapter = new SpellTargetAdapter(spellCatalog, sourceRegistry);
        var phb = adapter.exact("Shield", "PHB");
        var xphb = adapter.exact("Shield", "XPHB");

        assertEquals(1, phb.size());
        assertEquals(1, xphb.size());
        assertFalse(phb.getFirst().stableId().equals(xphb.getFirst().stableId()));
        assertThrows(UnsupportedOperationException.class, () -> adapter.identities().clear());
        assertThrows(UnsupportedOperationException.class, () -> adapter.candidates("Shield").clear());
    }

    @Test
    void uidParserRejectsMalformedQualifiedIdentities() {
        assertThrows(IllegalArgumentException.class, () -> ReferenceUidParser.parse("|PHB"));
        assertThrows(IllegalArgumentException.class, () -> ReferenceUidParser.parse("shield|"));
        assertThrows(IllegalArgumentException.class, () -> ReferenceUidParser.parse("shield|PHB|XGE"));
    }

    @Test
    void resolverSeparatesExactAmbiguousUniqueMissingAndChoiceResults() {
        var adapter = new TestAdapter(List.of(
                new TargetIdentity(ReferenceTargetKind.SPELL, "spell:phb:shield", "Shield", "PHB"),
                new TargetIdentity(ReferenceTargetKind.SPELL, "spell:xphb:shield", "Shield", "XPHB"),
                new TargetIdentity(ReferenceTargetKind.SPELL, "spell:phb:alarm", "Alarm", "PHB")));
        var resolver = new ReferenceResolver(new AdapterRegistry(List.of(adapter)), sourceRegistry);

        assertEquals(ResolutionOutcome.RESOLVED,
                resolver.resolve(reference("Shield|PHB", "PHB", ReferenceSemanticForm.DEFINITION)).outcome());
        var ambiguous = resolver.resolve(reference("Shield", null, ReferenceSemanticForm.DEFINITION));
        assertEquals(ResolutionOutcome.AMBIGUOUS, ambiguous.outcome());
        assertEquals(CandidateCardinality.MANY, ambiguous.candidateCardinality());
        assertEquals(List.of("spell:phb:shield", "spell:xphb:shield"), ambiguous.candidateIds());

        var unique = resolver.resolve(reference("Alarm", null, ReferenceSemanticForm.DEFINITION));
        assertEquals(ResolutionOutcome.DEFERRED, unique.outcome());
        assertEquals(CandidateCardinality.ONE, unique.candidateCardinality());
        assertEquals(null, unique.resolvedTargetId());

        assertEquals(ResolutionOutcome.UNRESOLVED,
                resolver.resolve(reference("Missing|PHB", "PHB", ReferenceSemanticForm.DEFINITION)).outcome());
        var choice = resolver.resolve(reference("Shield|PHB", "PHB", ReferenceSemanticForm.CHOICE));
        assertEquals(ResolutionOutcome.DEFERRED, choice.outcome());
        assertEquals("spell:phb:shield", choice.candidateIds().getFirst());
        assertEquals(null, choice.resolvedTargetId());
        assertThrows(IllegalStateException.class,
                () -> resolver.resolve(reference("Shield|NOT_A_SOURCE", "NOT_A_SOURCE",
                        ReferenceSemanticForm.DEFINITION)));
    }

    @Test
    void snapshotCollectionsAreImmutableAndDeterministicallyOrdered() {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.results().clear());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.diagnostics().clear());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.countsByOutcome().clear());
        assertEquals(snapshot.results().stream().map(result -> result.reference().owner().catalog()).sorted().toList(),
                snapshot.results().stream().map(result -> result.reference().owner().catalog()).toList());
    }

    @Test
    void b2CatalogAdaptersUseCatalogOwnedIdsAndImmutableIndexes() {
        var featAdapter = new FeatTargetAdapter(featCatalog, sourceRegistry);
        var optionalAdapter = new OptionalFeatureTargetAdapter(optionalFeatureCatalog, sourceRegistry);
        var skillAdapter = new SkillTargetAdapter(skillCatalog, sourceRegistry);
        var languageAdapter = new LanguageTargetAdapter(languageCatalog, sourceRegistry);

        assertEquals(featCatalog.size(), featAdapter.identities().size());
        assertEquals(optionalFeatureCatalog.size(), optionalAdapter.identities().size());
        assertEquals(skillCatalog.size(), skillAdapter.identities().size());
        assertEquals(languageCatalog.size(), languageAdapter.identities().size());
        assertTrue(featAdapter.identities().stream()
                .allMatch(identity -> featCatalog.findById(identity.stableId()) != null));
        assertTrue(optionalAdapter.identities().stream()
                .allMatch(identity -> optionalFeatureCatalog.findById(identity.stableId()) != null));
        assertThrows(UnsupportedOperationException.class, () -> featAdapter.identities().clear());
        assertThrows(UnsupportedOperationException.class, () -> skillAdapter.candidates("Acrobatics").clear());

        List<TargetIdentity> editionFeats = featAdapter.candidates("Alert");
        assertTrue(editionFeats.stream().anyMatch(identity -> identity.source().equalsIgnoreCase("PHB")));
        assertTrue(editionFeats.stream().anyMatch(identity -> identity.source().equalsIgnoreCase("XPHB")));
        assertTrue(editionFeats.stream().map(TargetIdentity::stableId).distinct().count() >= 2);

        for (CatalogTargetAdapter adapter : List.of(featAdapter, optionalAdapter, skillAdapter, languageAdapter)) {
            TargetIdentity identity = adapter.identities().getFirst();
            ParsedUid parsed = ReferenceUidParser.parse(identity.name() + "|" + identity.source());
            var authored = new AuthoredReference(new ReferenceOwner("test", "adapter-test"), "test.reference",
                    adapter.targetKind(), ReferenceSemanticForm.DEFINITION, parsed.originalToken(), identity.source(),
                    parsed.name(), parsed.source(), null);
            var result = new ReferenceResolver(new AdapterRegistry(List.of(adapter)), sourceRegistry).resolve(authored);
            assertEquals(ResolutionOutcome.RESOLVED, result.outcome());
            assertEquals(identity.stableId(), result.resolvedTargetId());
            var unknownSource = ReferenceUidParser.parse(identity.name() + "|NOT_A_SOURCE");
            var invalidReference = new AuthoredReference(new ReferenceOwner("test", "adapter-test"),
                    "test.unknownSource", adapter.targetKind(), ReferenceSemanticForm.DEFINITION,
                    unknownSource.originalToken(), unknownSource.source(), unknownSource.name(), unknownSource.source(), null);
            assertThrows(IllegalStateException.class,
                    () -> new ReferenceResolver(new AdapterRegistry(List.of(adapter)), sourceRegistry)
                            .resolve(invalidReference));
        }
    }

    @Test
    void b2SnapshotPreservesChoicesFiltersPrerequisitesAndSourceSemantics() {
        ResolutionSnapshot snapshot = coordinator.snapshot();
        List<ResolutionResult> featGrants = snapshot.results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.FEAT)
                .toList();
        List<ResolutionResult> optionalReferences = snapshot.results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.OPTIONAL_FEATURE)
                .toList();
        List<ResolutionResult> skillReferences = snapshot.results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.SKILL)
                .toList();
        List<ResolutionResult> languageReferences = snapshot.results().stream()
                .filter(result -> result.reference().targetKind() == ReferenceTargetKind.LANGUAGE)
                .toList();

        assertFalse(featGrants.isEmpty());
        assertFalse(optionalReferences.isEmpty());
        assertFalse(skillReferences.isEmpty());
        assertFalse(languageReferences.isEmpty());
        assertTrue(featGrants.stream().anyMatch(result -> result.reference().owner().catalog().equals("background")
                && result.reference().fieldLocator().startsWith("feats")));
        assertTrue(optionalReferences.stream().anyMatch(result -> result.reference().owner().catalog().equals("class")
                || result.reference().owner().catalog().equals("subclass")
                || result.reference().owner().catalog().equals("class-feature")
                || result.reference().owner().catalog().equals("subclass-feature")));
        assertTrue(skillReferences.stream().anyMatch(result -> result.reference().semanticForm() == ReferenceSemanticForm.CHOICE));
        assertTrue(featGrants.stream().filter(result -> result.reference().semanticForm() == ReferenceSemanticForm.CHOICE)
                .allMatch(result -> result.outcome() == ResolutionOutcome.DEFERRED));
        assertTrue(featGrants.stream().filter(result -> result.reference().owner().catalog().equals("feat"))
                .allMatch(result -> result.reference().deferredReason() != null
                        || result.reference().fieldLocator().contains("prerequisites")));
        assertTrue(optionalReferences.stream().anyMatch(result -> result.outcome() == ResolutionOutcome.UNRESOLVED
                && result.reference().fieldLocator().contains("prerequisites")));
        assertTrue(languageReferences.stream().filter(result -> result.reference().parsedSource() == null)
                .allMatch(result -> result.outcome() == ResolutionOutcome.AMBIGUOUS
                        || result.outcome() == ResolutionOutcome.DEFERRED
                        || result.outcome() == ResolutionOutcome.UNRESOLVED));
        assertEquals(snapshot.results().size(), snapshot.diagnostics().size());
        List<ResolutionResult> historical = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("cult")
                        && !result.reference().owner().catalog().equals("boon")
                        && !result.reference().owner().catalog().equals("object")
                        && !result.reference().owner().catalog().equals("facility")
                        && !result.reference().owner().catalog().equals("encounter")
                        && !result.reference().owner().catalog().equals("reward")
                        && !result.reference().owner().catalog().equals("trap")
                        && !result.reference().owner().catalog().equals("hazard")
                        && !result.reference().owner().catalog().equals("vehicle")
                        && !result.reference().owner().catalog().equals("vehicle-upgrade")
                        && !result.reference().owner().catalog().equals("variant-rule")
                        && !result.reference().owner().catalog().equals("deity")
                        && !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(160, countTargetKind(historical, ReferenceTargetKind.FEAT));
        assertEquals(210, countTargetKind(historical, ReferenceTargetKind.OPTIONAL_FEATURE));
        assertEquals(830, countTargetKind(historical, ReferenceTargetKind.SKILL));
        assertEquals(273, countTargetKind(historical, ReferenceTargetKind.LANGUAGE));
        assertEquals(10, countTargetKind(historical, ReferenceTargetKind.PROFICIENCY));
        assertEquals(1697, countOutcome(historical, ResolutionOutcome.RESOLVED));
        assertEquals(1108, countOutcome(historical, ResolutionOutcome.AMBIGUOUS));
        assertEquals(30, countOutcome(historical, ResolutionOutcome.UNRESOLVED));
        assertEquals(1040, countOutcome(historical, ResolutionOutcome.DEFERRED));
        List<ResolutionResult> rewardResults = rewardReferences(snapshot);
        assertEquals(291, rewardResults.size());
        assertEquals(61, countOutcome(rewardResults, ResolutionOutcome.RESOLVED));
        assertEquals(222, countOutcome(rewardResults, ResolutionOutcome.AMBIGUOUS));
        assertEquals(5, countOutcome(rewardResults, ResolutionOutcome.UNRESOLVED));
        assertEquals(3, countOutcome(rewardResults, ResolutionOutcome.DEFERRED));
        List<ResolutionResult> originalResults = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("reward"))
                .filter(result -> !result.reference().owner().catalog().equals("trap"))
                .filter(result -> !result.reference().owner().catalog().equals("hazard"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle-upgrade"))
                .filter(result -> !result.reference().owner().catalog().equals("variant-rule"))
                .filter(result -> !result.reference().owner().catalog().equals("deity"))
                .filter(result -> !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(4079, originalResults.size());
        assertEquals(1796, countOutcome(originalResults, ResolutionOutcome.RESOLVED));
        assertEquals(1182, countOutcome(originalResults, ResolutionOutcome.AMBIGUOUS));
        assertEquals(39, countOutcome(originalResults, ResolutionOutcome.UNRESOLVED));
        assertEquals(1062, countOutcome(originalResults, ResolutionOutcome.DEFERRED));
        assertEquals(4, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("object")).count());
        assertEquals(110, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("facility")).count());
        assertEquals(15, snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("encounter")).count());
        assertEquals(3, countOutcome(encounterReferences(snapshot), ResolutionOutcome.RESOLVED));
        assertEquals(4, countOutcome(encounterReferences(snapshot), ResolutionOutcome.AMBIGUOUS));
        assertEquals(3, countOutcome(encounterReferences(snapshot), ResolutionOutcome.UNRESOLVED));
        assertEquals(5, countOutcome(encounterReferences(snapshot), ResolutionOutcome.DEFERRED));
        List<ResolutionResult> previousSnapshot = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("encounter"))
                .filter(result -> !result.reference().owner().catalog().equals("reward"))
                .filter(result -> !result.reference().owner().catalog().equals("trap"))
                .filter(result -> !result.reference().owner().catalog().equals("hazard"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle-upgrade"))
                .filter(result -> !result.reference().owner().catalog().equals("variant-rule"))
                .filter(result -> !result.reference().owner().catalog().equals("deity"))
                .filter(result -> !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(4064, previousSnapshot.size());
        assertEquals(1793, countOutcome(previousSnapshot, ResolutionOutcome.RESOLVED));
        assertEquals(1178, countOutcome(previousSnapshot, ResolutionOutcome.AMBIGUOUS));
        assertEquals(36, countOutcome(previousSnapshot, ResolutionOutcome.UNRESOLVED));
        assertEquals(1057, countOutcome(previousSnapshot, ResolutionOutcome.DEFERRED));
        List<ResolutionResult> preTrapHazardSnapshot = snapshot.results().stream()
                .filter(result -> !result.reference().owner().catalog().equals("trap"))
                .filter(result -> !result.reference().owner().catalog().equals("hazard"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle"))
                .filter(result -> !result.reference().owner().catalog().equals("vehicle-upgrade"))
                .filter(result -> !result.reference().owner().catalog().equals("variant-rule"))
                .filter(result -> !result.reference().owner().catalog().equals("deity"))
                .filter(result -> !result.reference().owner().catalog().equals("treasure-table"))
                .toList();
        assertEquals(4370, preTrapHazardSnapshot.size());
        assertEquals(1857, countOutcome(preTrapHazardSnapshot, ResolutionOutcome.RESOLVED));
        assertEquals(1404, countOutcome(preTrapHazardSnapshot, ResolutionOutcome.AMBIGUOUS));
        assertEquals(44, countOutcome(preTrapHazardSnapshot, ResolutionOutcome.UNRESOLVED));
        assertEquals(1065, countOutcome(preTrapHazardSnapshot, ResolutionOutcome.DEFERRED));
        List<String> occurrenceLocators = snapshot.results().stream()
                .map(result -> result.reference().owner().catalog() + "|"
                        + result.reference().owner().stableId() + "|"
                        + result.reference().targetKind() + "|"
                        + result.reference().fieldLocator())
                .toList();
        assertEquals(occurrenceLocators.size(), occurrenceLocators.stream().distinct().count());
        assertTrue(snapshot.results().stream().allMatch(result -> !result.reference().fieldLocator().isBlank()));
    }

    private static long countTargetKind(List<ResolutionResult> results, ReferenceTargetKind targetKind) {
        return results.stream().filter(result -> result.reference().targetKind() == targetKind).count();
    }

    private static List<ResolutionResult> encounterReferences(ResolutionSnapshot snapshot) {
        return snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("encounter"))
                .toList();
    }

    private static List<ResolutionResult> rewardReferences(ResolutionSnapshot snapshot) {
        return snapshot.results().stream()
                .filter(result -> result.reference().owner().catalog().equals("reward"))
                .toList();
    }

    private static long countOutcome(List<ResolutionResult> results, ResolutionOutcome outcome) {
        return results.stream().filter(result -> result.outcome() == outcome).count();
    }

    private static long count(
            ResolutionSnapshot snapshot,
            String fieldLocatorPart,
            ResolutionOutcome outcome,
            ReferenceTargetKind targetKind,
            String ownerCatalog,
            ReferenceSemanticForm semanticForm) {
        return snapshot.results().stream()
                .filter(result -> Arrays.stream(ownerCatalog.split("\\|"))
                        .anyMatch(value -> value.equals(result.reference().owner().catalog())))
                .filter(result -> result.reference().fieldLocator().contains(fieldLocatorPart))
                .filter(result -> result.outcome() == outcome)
                .filter(result -> result.reference().targetKind() == targetKind)
                .filter(result -> result.reference().semanticForm() == semanticForm)
                .count();
    }

    private static AuthoredReference reference(
            String token,
            String source,
            ReferenceSemanticForm semanticForm) {
        ParsedUid parsed = ReferenceUidParser.parse(token);
        return new AuthoredReference(new ReferenceOwner("test", "test-owner"), "test.field",
                ReferenceTargetKind.SPELL, semanticForm, token, source, parsed.name(), parsed.source(),
                semanticForm == ReferenceSemanticForm.CHOICE ? "choice candidate" : null);
    }

    private record TestAdapter(List<TargetIdentity> identities) implements CatalogTargetAdapter {
        private TestAdapter {
            identities = List.copyOf(identities);
        }

        @Override
        public ReferenceTargetKind targetKind() {
            return ReferenceTargetKind.SPELL;
        }

        @Override
        public List<TargetIdentity> exact(String name, String source) {
            return identities.stream()
                    .filter(value -> value.name().equalsIgnoreCase(name))
                    .filter(value -> value.source().equalsIgnoreCase(source))
                    .toList();
        }

        @Override
        public List<TargetIdentity> candidates(String name) {
            return identities.stream().filter(value -> value.name().equalsIgnoreCase(name))
                    .sorted(java.util.Comparator.comparing(TargetIdentity::stableId)).toList();
        }
    }
}
