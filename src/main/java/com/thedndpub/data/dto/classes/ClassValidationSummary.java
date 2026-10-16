package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassValidationSummary(
        int authoredFeatureReferences,
        int effectiveFeatureReferences,
        int authoredDirectSpellReferences,
        int authoredSpellChoiceCandidates,
        int subclassCopies,
        int featureCopies,
        int orphanFeatures,
        int primarySourceIdentities,
        int allSourceIdentities) {
}
