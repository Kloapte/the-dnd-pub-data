package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.CoverageStatus;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalMechanicsMetadataDto(
        SpellId ownerId, String engineVersion, String datasetVersion, String sourceResource, String sourceSha256,
        CoverageStatus coverage, String coverageScope, String reviewStatus,
        String licensingStatus, boolean publicationAuthorized, List<String> strategyIds,
        List<String> canonicalComparison) {
    public ExperimentalMechanicsMetadataDto {
        strategyIds = strategyIds.stream().distinct().sorted().toList();
        canonicalComparison = canonicalComparison.stream().distinct().sorted().toList();
        if (!"PENDING_REVIEW".equals(reviewStatus) || !"NEEDS_REVIEW".equals(licensingStatus)
                || publicationAuthorized) throw new IllegalArgumentException("Experimental metadata cannot approve a claim");
    }
}
