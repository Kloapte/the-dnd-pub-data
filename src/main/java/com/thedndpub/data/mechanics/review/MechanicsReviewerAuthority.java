package com.thedndpub.data.mechanics.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsReviewerAuthority(
        String reviewerId,
        String authorityReference,
        String mandateSha256) {
    public MechanicsReviewerAuthority {
        ModelChecks.text(reviewerId, "reviewerId");
        ModelChecks.text(authorityReference, "authorityReference");
        ModelChecks.digest(mandateSha256);
    }
}
