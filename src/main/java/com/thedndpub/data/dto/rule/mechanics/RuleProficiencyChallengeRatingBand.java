package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleInclusiveChallengeRatingBand.class),
        @JsonSubTypes.Type(value = RuleExclusiveChallengeRatingBand.class)
})
public sealed interface RuleProficiencyChallengeRatingBand
        permits RuleInclusiveChallengeRatingBand, RuleExclusiveChallengeRatingBand {
    Double maximumChallengeRatingInclusive();

    Integer bonus();
}
