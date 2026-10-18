package com.thedndpub.data.mechanics.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsReviewSummary(
        @JsonProperty(required = true) int total,
        @JsonProperty(required = true) int pendingReview,
        @JsonProperty(required = true) int needsMechanicsReview,
        @JsonProperty(required = true) int verified,
        @JsonProperty(required = true) int rejected,
        MechanicsReviewStatus aggregateStatus) {
    public MechanicsReviewSummary {
        ModelChecks.require(total >= 0 && pendingReview >= 0 && needsMechanicsReview >= 0 && verified >= 0 && rejected >= 0,
                "Review counts cannot be negative");
        ModelChecks.require((long) total == (long) pendingReview + needsMechanicsReview + verified + rejected,
                "Review totals do not reconcile");
        MechanicsReviewStatus expected = aggregate(total, pendingReview, needsMechanicsReview, verified, rejected);
        ModelChecks.require(aggregateStatus == expected, "Incorrect aggregate review status");
    }

    public static MechanicsReviewSummary from(List<MechanicsReviewState> states) {
        int pending = 0;
        int needsReview = 0;
        int verified = 0;
        int rejected = 0;
        for (MechanicsReviewState state : states) {
            switch (state.effectiveStatus()) {
                case PENDING_REVIEW -> pending++;
                case NEEDS_MECHANICS_REVIEW -> needsReview++;
                case VERIFIED -> verified++;
                case REJECTED -> rejected++;
            }
        }
        return new MechanicsReviewSummary(states.size(), pending, needsReview, verified, rejected,
                aggregate(states.size(), pending, needsReview, verified, rejected));
    }

    private static MechanicsReviewStatus aggregate(int total, int pending, int needsReview, int verified, int rejected) {
        if (total == 0) {
            return null;
        }
        if (verified == total) {
            return MechanicsReviewStatus.VERIFIED;
        }
        if (rejected == total) {
            return MechanicsReviewStatus.REJECTED;
        }
        if (needsReview > 0 || rejected > 0) {
            return MechanicsReviewStatus.NEEDS_MECHANICS_REVIEW;
        }
        return MechanicsReviewStatus.PENDING_REVIEW;
    }
}
