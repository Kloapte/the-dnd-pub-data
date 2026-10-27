package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionModifierDirection {
    @JsonProperty("advantage") ADVANTAGE,
    @JsonProperty("disadvantage") DISADVANTAGE,
    @JsonProperty("bonus") BONUS,
    @JsonProperty("penalty") PENALTY
}
