package com.thedndpub.data.mechanics.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HumanMechanicsReviewDecision(
        String id,
        MechanicRevisionReference claim,
        String reviewerId,
        String authorityReference,
        MechanicsReviewAction action,
        String rationale,
        String reviewedAt,
        @JsonProperty(required = true) boolean acceptsPartialScope,
        String supersedesDecisionId) {
    public HumanMechanicsReviewDecision {
        ModelChecks.text(id, "decisionId");
        ModelChecks.required(claim, "claim");
        ModelChecks.text(reviewerId, "reviewerId");
        ModelChecks.text(authorityReference, "authorityReference");
        ModelChecks.required(action, "action");
        ModelChecks.text(rationale, "rationale");
        ModelChecks.text(reviewedAt, "reviewedAt");
        ModelChecks.require(reviewedAt.endsWith("Z"), "Human review timestamp must be UTC");
        Instant.parse(reviewedAt);
        if (supersedesDecisionId != null) {
            ModelChecks.text(supersedesDecisionId, "supersedesDecisionId");
            ModelChecks.require(!id.equals(supersedesDecisionId), "A decision cannot supersede itself");
        }
    }
}
