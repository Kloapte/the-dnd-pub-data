package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionDifficultyClassKind {
    @JsonProperty("FIXED") FIXED,
    @JsonProperty("EFFECT_DEFINED") EFFECT_DEFINED,
    @JsonProperty("ABILITY_PLUS_PROFICIENCY") ABILITY_PLUS_PROFICIENCY,
    @JsonProperty("FIXED_PLUS_SPELL_LEVEL") FIXED_PLUS_SPELL_LEVEL,
    @JsonProperty("GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE") GREATER_OF_FIXED_AND_TARGET_ABILITY_SCORE
}
