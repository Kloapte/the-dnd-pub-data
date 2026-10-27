package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleExclusiveChallengeRatingBand(
        Double minimumChallengeRatingExclusive,
        Double maximumChallengeRatingInclusive,
        Integer bonus) implements RuleProficiencyChallengeRatingBand {
    public RuleExclusiveChallengeRatingBand {
        requireRating(minimumChallengeRatingExclusive, "minimumChallengeRatingExclusive");
        requireRating(maximumChallengeRatingInclusive, "maximumChallengeRatingInclusive");
        requireBonus(bonus);
    }

    private static void requireRating(Double rating, String property) {
        Objects.requireNonNull(rating, property + " is required");
        if (rating < 0 || rating > 30) throw new IllegalArgumentException(property + " must be from 0 through 30");
    }

    private static void requireBonus(Integer value) {
        Objects.requireNonNull(value, "bonus is required");
        if (value < 2 || value > 9) throw new IllegalArgumentException("bonus must be from 2 through 9");
    }
}
