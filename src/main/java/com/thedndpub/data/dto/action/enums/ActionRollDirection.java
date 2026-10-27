package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionRollDirection {
    @JsonProperty("advantage") ADVANTAGE,
    @JsonProperty("disadvantage") DISADVANTAGE,
    @JsonProperty("bonus") BONUS,
    @JsonProperty("penalty") PENALTY
}
