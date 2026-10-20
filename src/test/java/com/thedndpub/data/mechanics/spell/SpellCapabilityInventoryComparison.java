package com.thedndpub.data.mechanics.spell;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Deterministic summary of the preserved 7.4.4A inventory against the expanded assessment. */
public record SpellCapabilityInventoryComparison(
        String baselineReport,
        String currentReport,
        int catalogTotal,
        Map<String, Integer> baselineClassificationCounts,
        Map<String, Integer> currentClassificationCounts,
        Map<String, Integer> classificationTransitions,
        int baselineProcessedOwners,
        int currentProcessedOwners,
        int baselineProcessedTypedClaims,
        int currentProcessedTypedClaims,
        int newlyProducedTypedClaims,
        int newlyConvertibleSpellOwners,
        int baselinePotentialTypedClaims,
        int currentPotentialTypedClaims,
        int additionalPotentialTypedClaims,
        int savingThrowStructuredGapReasonReduction,
        int damageStructuredGapReasonReduction,
        int savingThrowClaimsGainedOwners,
        int damageClaimsGainedOwners,
        List<String> previouslyProcessedOwnerRegressions,
        int remainingUnsupportedOwners,
        int remainingNotAssessedOwners) {
    public SpellCapabilityInventoryComparison {
        baselineClassificationCounts = Collections.unmodifiableMap(new TreeMap<>(baselineClassificationCounts));
        currentClassificationCounts = Collections.unmodifiableMap(new TreeMap<>(currentClassificationCounts));
        classificationTransitions = Collections.unmodifiableMap(new TreeMap<>(classificationTransitions));
        previouslyProcessedOwnerRegressions = List.copyOf(previouslyProcessedOwnerRegressions);
    }

    public static SpellCapabilityInventoryComparison compare(
            SpellCapabilityInventory baseline,
            SpellCapabilityInventory current,
            int currentProcessedTypedClaims) {
        if (baseline.catalogTotal() != current.catalogTotal()) {
            throw new IllegalArgumentException("Baseline and current Spell catalogs have different totals");
        }
        Map<String, SpellCapabilityRecord> before = index(baseline.spells());
        Map<String, SpellCapabilityRecord> after = index(current.spells());
        if (!before.keySet().equals(after.keySet())) {
            throw new IllegalArgumentException("Baseline and current Spell identities differ");
        }
        Map<String, Integer> transitions = new TreeMap<>();
        List<String> regressions = new ArrayList<>();
        int newlyConvertible = 0;
        int saveGapReduced = 0;
        int damageGapReduced = 0;
        for (String id : before.keySet().stream().sorted().toList()) {
            SpellCapabilityRecord oldRecord = before.get(id);
            SpellCapabilityRecord newRecord = after.get(id);
            transitions.merge(oldRecord.classification().name() + "->" + newRecord.classification().name(),
                    1, Integer::sum);
            if (oldRecord.potentialMechanicCount() == 0 && newRecord.potentialMechanicCount() > 0) {
                newlyConvertible++;
            }
            if (oldRecord.unsupportedReasons().contains(
                    "SAVING_THROW_REQUIREMENT:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN")
                    && newRecord.candidateMechanicFamilies().contains("SAVING_THROW_REQUIREMENT")) {
                saveGapReduced++;
            }
            if (oldRecord.unsupportedReasons().contains("DAMAGE:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN")
                    && newRecord.candidateMechanicFamilies().contains("DAMAGE")) {
                damageGapReduced++;
            }
            if (oldRecord.processingState().equals("PROCESSED")
                    && newRecord.potentialMechanicCount() < oldRecord.potentialMechanicCount()) {
                regressions.add(id + ":" + oldRecord.potentialMechanicCount() + "->"
                        + newRecord.potentialMechanicCount());
            }
        }
        return new SpellCapabilityInventoryComparison(
                "7.4.4A-spell-capability-inventory-baseline.json",
                "7.4.4A-spell-capability-inventory.json",
                current.catalogTotal(), baseline.classificationCounts(), current.classificationCounts(), transitions,
                baseline.currentlyProcessedCount(), current.currentlyProcessedCount(),
                baseline.potentialTypedClaimCount(), currentProcessedTypedClaims,
                currentProcessedTypedClaims - baseline.potentialTypedClaimCount(), newlyConvertible,
                baseline.potentialTypedClaimCount(), current.potentialTypedClaimCount(),
                current.potentialTypedClaimCount() - baseline.potentialTypedClaimCount(),
                baseline.unsupportedReasonCounts().getOrDefault(
                        "SAVING_THROW_REQUIREMENT:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN", 0)
                        - current.unsupportedReasonCounts().getOrDefault(
                        "SAVING_THROW_REQUIREMENT:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN", 0),
                baseline.unsupportedReasonCounts().getOrDefault(
                        "DAMAGE:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN", 0)
                        - current.unsupportedReasonCounts().getOrDefault(
                        "DAMAGE:SOURCE_CLASSIFICATION_WITHOUT_MATCHING_PATTERN", 0),
                saveGapReduced, damageGapReduced, regressions,
                current.classificationCounts().getOrDefault("UNSUPPORTED", 0),
                current.classificationCounts().getOrDefault("NOT_ASSESSED", 0));
    }

    private static Map<String, SpellCapabilityRecord> index(List<SpellCapabilityRecord> records) {
        return records.stream().collect(Collectors.toMap(SpellCapabilityRecord::spellId, Function.identity()));
    }
}
