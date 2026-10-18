package com.thedndpub.data.mechanics.review;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.mechanics.model.ModelChecks;

/** A policy-validated decision, not a user-supplied VERIFIED flag. */
@JsonDeserialize(using = ValidatedHumanMechanicsReviewDeserializer.class)
public final class ValidatedHumanMechanicsReview {
    private final HumanMechanicsReviewDecision decision;

    private ValidatedHumanMechanicsReview(HumanMechanicsReviewDecision decision) {
        this.decision = decision;
    }

    public static ValidatedHumanMechanicsReview validate(
            HumanMechanicsReviewDecision decision,
            MechanicsReviewPolicy policy) {
        ModelChecks.required(policy, "policy").validate(decision);
        return new ValidatedHumanMechanicsReview(decision);
    }

    @JsonValue
    public HumanMechanicsReviewDecision decision() {
        return decision;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ValidatedHumanMechanicsReview review && decision.equals(review.decision);
    }

    @Override
    public int hashCode() {
        return decision.hashCode();
    }
}
