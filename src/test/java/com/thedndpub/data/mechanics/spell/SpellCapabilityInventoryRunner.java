package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.util.*;

/** Offline, per-owner inventory execution over the same immutable matcher registry as production extraction. */
public final class SpellCapabilityInventoryRunner {
    private final ObjectMapper mapper;
    private final ResourceLoader resources;
    private final SpellMechanicsPatternRegistry patterns;

    public SpellCapabilityInventoryRunner(ObjectMapper mapper) {
        this(mapper, new DefaultResourceLoader());
    }

    public SpellCapabilityInventoryRunner(ObjectMapper mapper, ResourceLoader resources) {
        this(mapper, resources, SpellMechanicsPatternRegistry.standard());
    }

    SpellCapabilityInventoryRunner(
            ObjectMapper mapper, ResourceLoader resources, List<SpellMechanicsPattern> patternList) {
        this(mapper, resources, new SpellMechanicsPatternRegistry(patternList));
    }

    private SpellCapabilityInventoryRunner(
            ObjectMapper mapper, ResourceLoader resources, SpellMechanicsPatternRegistry patterns) {
        this.mapper = Objects.requireNonNull(mapper);
        this.resources = Objects.requireNonNull(resources);
        this.patterns = Objects.requireNonNull(patterns);
    }

    public SpellCapabilityInventory assess(
            List<SpellDefinition> definitions,
            List<MechanicsDocument> currentDocuments,
            List<MechanicsDocument> unattemptedDocuments,
            List<SpellMechanicsSourceOwner> sourceOwners) {
        Map<SpellId, MechanicsDocument> currentById = indexDocuments(currentDocuments);
        Map<SpellId, MechanicsDocument> unattemptedById = indexDocuments(unattemptedDocuments);
        Map<SpellId, SpellMechanicsSourceOwner> sourceById = new HashMap<>();
        for (var source : sourceOwners) {
            if (sourceById.putIfAbsent(source.id(), source) != null) {
                throw new IllegalArgumentException("Duplicate source owner " + source.id().value());
            }
        }
        if (currentById.size() != definitions.size() || unattemptedById.size() != definitions.size()
                || sourceById.size() != definitions.size()) {
            throw new IllegalArgumentException("Inventory owner sets do not match the normalized Spell catalog");
        }

        Map<String, byte[]> bytesByResource = new HashMap<>();
        List<SpellCapabilityRecord> records = new ArrayList<>();
        Map<String, Integer> sources = new TreeMap<>();
        Map<String, Integer> classifications = zeroCounts(SpellCapabilityClassification.values());
        Map<String, Integer> families = new TreeMap<>();
        Map<String, Integer> unsupportedReasons = new TreeMap<>();
        Map<String, Integer> unassessedReasons = new TreeMap<>();
        int processed = 0;
        int newlyConvertible = 0;
        int potentialClaims = 0;

        List<SpellDefinition> ordered = definitions.stream()
                .sorted(Comparator.comparing(definition -> definition.id().value())).toList();
        for (SpellDefinition definition : ordered) {
            SpellId id = definition.id();
            MechanicsDocument current = currentById.get(id);
            MechanicsDocument original = unattemptedById.get(id);
            SpellMechanicsSourceOwner source = sourceById.get(id);
            boolean isProcessed = current.processing().state() == ProcessingState.PROCESSED;
            if (isProcessed) {
                processed++;
            }
            String sourceCode = definition.source().identity().code();
            sources.merge(sourceCode, 1, Integer::sum);
            SpellMechanicsPattern pattern = null;
            MechanicsDocument candidate = null;
            List<String> diagnostics = new ArrayList<>();
            try {
                pattern = patterns.find(definition, matchingPattern ->
                        supportsForInventory(matchingPattern, definition));
                if (pattern != null) {
                    byte[] sourceBytes = bytesByResource.computeIfAbsent(
                            source.revision().resourcePath(), this::readPinnedResource);
                    candidate = extractForInventory(pattern, definition,
                            new SpellMechanicsEvidenceSource(definition, source, sourceBytes, mapper), original);
                }
            } catch (RuntimeException exception) {
                diagnostics.add(exception instanceof SpellPatternMismatch mismatch
                        ? "PATTERN_MISMATCH_" + mismatch.reason().name() : "EXTRACTION_FAILURE");
            }

            List<MechanicRecord> claims = candidate == null ? List.of() : candidate.interpretations();
            if (!isProcessed && !claims.isEmpty()) {
                newlyConvertible++;
            }
            List<String> candidateFamilies = claims.stream().map(record -> familyName(record.payload()))
                    .distinct().sorted().toList();
            Map<String, Integer> completeness = new TreeMap<>();
            claims.forEach(claim -> completeness.merge(claim.assessment().completeness().name(), 1, Integer::sum));
            List<String> unsupportedFamilies = candidate == null ? List.of() : candidate.unsupported().stream()
                    .map(item -> item.candidateFamily().name()).distinct().sorted().toList();
            List<String> unsupported = new ArrayList<>();
            if (candidate != null) {
                candidate.unsupported().stream().map(item -> item.candidateFamily().name() + ":" + item.reason().name())
                        .distinct().sorted().forEach(unsupported::add);
            }
            if (pattern == null) {
                List<String> structuredGaps = structuredGapFamilies(definition);
                unsupported.addAll(structuredGaps);
                unsupportedFamilies = structuredGaps.stream().map(value -> value.substring(0, value.indexOf(':')))
                        .distinct().sorted().toList();
            }
            List<String> unassessed = new ArrayList<>();
            if (candidate == null) {
                if (pattern == null) {
                    unassessed.add("NO_MATCHING_PATTERN_SOURCE_CONTENT_UNASSESSED");
                    unassessed.add("SOURCE_PATH:" + source.jsonPointer() + "/entries");
                    unassessed.add("SOURCE_PATH:" + source.jsonPointer() + "/entriesHigherLevel");
                } else {
                    unassessed.add("EXTRACTION_FAILED");
                }
            } else {
                candidate.coverage().unassessedSourcePaths().forEach(path -> unassessed.add("SOURCE_PATH:" + path));
            }

            SpellCapabilityClassification classification = classify(candidate, pattern, diagnostics,
                    unsupported, unassessed, claims);
            increment(classifications, classification.name());
            candidateFamilies.forEach(family -> families.merge(family, 1, Integer::sum));
            unsupported.forEach(reason -> unsupportedReasons.merge(reason, 1, Integer::sum));
            unassessed.forEach(reason -> unassessedReasons.merge(aggregateUnassessedReason(reason), 1, Integer::sum));
            potentialClaims += claims.size();
            records.add(new SpellCapabilityRecord(id.value(), definition.identity().name(), sourceCode,
                    current.processing().state().name(), current.coverage().status().name(), pattern != null,
                    pattern == null ? null : pattern.patternId(),
                    candidate == null ? null : candidate.coverage().status().name(),
                    classification, candidateFamilies, claims.size(), completeness, unsupportedFamilies, unsupported,
                    unassessed, diagnostics));
        }
        return new SpellCapabilityInventory("5eTools v2.37.0", records.size(), sources, classifications,
                processed, newlyConvertible, potentialClaims, families, unsupportedReasons, unassessedReasons, records);
    }

    private static boolean supportsForInventory(SpellMechanicsPattern pattern, SpellDefinition spell) {
        return pattern instanceof PhbSpellMechanicsExtractor phb
                ? phb.supportsForInventory(spell)
                : pattern.supports(spell);
    }

    private static MechanicsDocument extractForInventory(
            SpellMechanicsPattern pattern,
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        return pattern instanceof PhbSpellMechanicsExtractor phb
                ? phb.extractForInventory(spell, source, original)
                : pattern.extract(spell, source, original);
    }

    private static SpellCapabilityClassification classify(
            MechanicsDocument candidate,
            SpellMechanicsPattern pattern,
            List<String> diagnostics,
            List<String> unsupported,
            List<String> unassessed,
            List<MechanicRecord> claims) {
        if (!diagnostics.isEmpty()) {
            return SpellCapabilityClassification.NOT_ASSESSED;
        }
        if (pattern == null) {
            return unsupported.isEmpty() ? SpellCapabilityClassification.NOT_ASSESSED
                    : SpellCapabilityClassification.UNSUPPORTED;
        }
        if (claims.isEmpty()) {
            return unsupported.isEmpty() ? SpellCapabilityClassification.NOT_ASSESSED
                    : SpellCapabilityClassification.UNSUPPORTED;
        }
        boolean completeClaims = claims.stream().allMatch(claim ->
                claim.assessment().completeness() == MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
        if (completeClaims && unsupported.isEmpty() && unassessed.isEmpty()
                && candidate.coverage().status() == CoverageStatus.COMPLETE_WITHIN_SCOPE) {
            return SpellCapabilityClassification.SUPPORTED;
        }
        return SpellCapabilityClassification.PARTIALLY_SUPPORTED;
    }

    private static List<String> structuredGapFamilies(SpellDefinition definition) {
        var mechanics = definition.mechanics();
        List<String> gaps = new ArrayList<>();
        addGap(gaps, "DAMAGE", mechanics.damageInflicted());
        addGap(gaps, "CONDITION", mechanics.conditionsInflicted());
        addGap(gaps, "CONDITION", mechanics.conditionImmunities());
        addGap(gaps, "DAMAGE_RESISTANCE", mechanics.damageResistances());
        addGap(gaps, "DAMAGE_RESISTANCE", mechanics.damageImmunities());
        addGap(gaps, "DAMAGE_RESISTANCE", mechanics.damageVulnerabilities());
        addGap(gaps, "SAVING_THROW_REQUIREMENT", mechanics.savingThrows());
        addGap(gaps, "ATTACK_ROLL", mechanics.spellAttacks());
        return gaps.stream().distinct().sorted().toList();
    }

    private static void addGap(List<String> gaps, String family, List<String> sourceTags) {
        if (!sourceTags.isEmpty()) {
            gaps.add(family + ":SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN");
        }
    }

    private static String aggregateUnassessedReason(String reason) {
        if (reason.equals("NO_MATCHING_PATTERN_SOURCE_CONTENT_UNASSESSED")) return reason;
        if (reason.equals("EXTRACTION_FAILED")) return reason;
        if (reason.contains("/entriesHigherLevel")) return "UNASSESSED_SOURCE_CONTENT:/entriesHigherLevel";
        if (reason.contains("/entries")) return "UNASSESSED_SOURCE_CONTENT:/entries";
        return "UNASSESSED_SOURCE_CONTENT:OTHER";
    }

    private static String familyName(MechanicPayload payload) {
        if (payload instanceof ArmorClassModifierMechanic) return "ARMOR_CLASS_MODIFIER";
        if (payload instanceof ReactionActivationMechanic) return "REACTION_ACTIVATION";
        if (payload instanceof EffectDurationMechanic) return "DURATION";
        if (payload instanceof SpellDamagePreventionMechanic) return "SPELL_DAMAGE_PREVENTION";
        if (payload instanceof DamageMechanic) return "DAMAGE";
        if (payload instanceof HealingMechanic) return "HEALING";
        if (payload instanceof SavingThrowRequirementMechanic) return "SAVING_THROW_REQUIREMENT";
        if (payload instanceof SpellSlotScalingMechanic) return "SPELL_SLOT_SCALING";
        if (payload instanceof ConditionChangeMechanic || payload instanceof ConditionChoiceMechanic) return "CONDITION";
        if (payload instanceof StatisticModifierMechanic) return "MODIFIER";
        if (payload instanceof DamageResistanceMechanic) return "DAMAGE_RESISTANCE";
        if (payload instanceof AttackRollMechanic) return "ATTACK_ROLL";
        if (payload instanceof HitPointRecoveryPreventionMechanic) return "HEALING_PREVENTION";
        if (payload instanceof ReactionPreventionMechanic) return "REACTION_RESTRICTION";
        if (payload instanceof AttackRollModifierMechanic) return "ATTACK_ROLL_MODIFIER";
        if (payload instanceof com.thedndpub.data.mechanics.model.DamageInstanceSeriesMechanic) {
            return "DAMAGE_INSTANCE_SERIES";
        }
        return "UNKNOWN";
    }

    private byte[] readPinnedResource(String path) {
        try {
            return resources.getResource("classpath:" + path).getContentAsByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read pinned Spell resource", exception);
        }
    }

    private static Map<SpellId, MechanicsDocument> indexDocuments(List<MechanicsDocument> documents) {
        Map<SpellId, MechanicsDocument> indexed = new HashMap<>();
        for (MechanicsDocument document : documents) {
            SpellId id = new SpellId(document.owner().id());
            if (indexed.putIfAbsent(id, document) != null) {
                throw new IllegalArgumentException("Duplicate mechanics document " + id.value());
            }
        }
        return indexed;
    }

    private static Map<String, Integer> zeroCounts(Enum<?>[] values) {
        Map<String, Integer> counts = new TreeMap<>();
        for (Enum<?> value : values) counts.put(value.name(), 0);
        return counts;
    }

    private static void increment(Map<String, Integer> counts, String key) {
        counts.merge(key, 1, Integer::sum);
    }
}
