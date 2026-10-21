package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalMechanicsComparisonDto(
        List<String> reproducedClaimIds,
        List<String> missingCanonicalClaimIds,
        List<String> additionalExperimentalClaimIds,
        List<String> changedClaimIds) {
    public ExperimentalMechanicsComparisonDto {
        reproducedClaimIds = sorted(reproducedClaimIds);
        missingCanonicalClaimIds = sorted(missingCanonicalClaimIds);
        additionalExperimentalClaimIds = sorted(additionalExperimentalClaimIds);
        changedClaimIds = sorted(changedClaimIds);
    }

    private static List<String> sorted(List<String> values) {
        return ModelChecks.list(values).stream().sorted().toList();
    }
}
