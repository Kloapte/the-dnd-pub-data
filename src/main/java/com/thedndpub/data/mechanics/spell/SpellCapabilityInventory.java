package com.thedndpub.data.mechanics.spell;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

public record SpellCapabilityInventory(
        String datasetVersion,
        int catalogTotal,
        Map<String, Integer> sourceCounts,
        Map<String, Integer> classificationCounts,
        int currentlyProcessedCount,
        int newlyConvertibleCount,
        int potentialTypedClaimCount,
        Map<String, Integer> mechanicFamilyCounts,
        Map<String, Integer> unsupportedReasonCounts,
        Map<String, Integer> unassessedReasonCounts,
        List<SpellCapabilityRecord> spells) {
    public SpellCapabilityInventory {
        sourceCounts = Collections.unmodifiableMap(new TreeMap<>(sourceCounts));
        classificationCounts = Collections.unmodifiableMap(new TreeMap<>(classificationCounts));
        mechanicFamilyCounts = Collections.unmodifiableMap(new TreeMap<>(mechanicFamilyCounts));
        unsupportedReasonCounts = Collections.unmodifiableMap(new TreeMap<>(unsupportedReasonCounts));
        unassessedReasonCounts = Collections.unmodifiableMap(new TreeMap<>(unassessedReasonCounts));
        spells = List.copyOf(spells);
    }
}
