package com.thedndpub.data.dto.monster.statistics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ChallengeRating(String value, String covenValue, String lairValue,
                              Integer experience, Integer lairExperience) {
}
