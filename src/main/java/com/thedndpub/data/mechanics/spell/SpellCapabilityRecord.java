package com.thedndpub.data.mechanics.spell;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

public record SpellCapabilityRecord(
        String spellId,
        String name,
        String source,
        String processingState,
        String currentCoverageStatus,
        boolean patternMatched,
        String patternId,
        String candidateCoverageStatus,
        SpellCapabilityClassification classification,
        List<String> candidateMechanicFamilies,
        int potentialMechanicCount,
        Map<String, Integer> claimCompletenessCounts,
        List<String> unsupportedMechanicFamilies,
        List<String> unsupportedReasons,
        List<String> unassessedSourceContent,
        List<String> diagnosticCodes) {
    public SpellCapabilityRecord {
        candidateMechanicFamilies = List.copyOf(candidateMechanicFamilies);
        claimCompletenessCounts = Collections.unmodifiableMap(new TreeMap<>(claimCompletenessCounts));
        unsupportedMechanicFamilies = List.copyOf(unsupportedMechanicFamilies);
        unsupportedReasons = List.copyOf(unsupportedReasons);
        unassessedSourceContent = List.copyOf(unassessedSourceContent);
        diagnosticCodes = List.copyOf(diagnosticCodes);
    }
}
