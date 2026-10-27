package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleMovementSpeedType {
    @JsonProperty("walking") WALKING,
    @JsonProperty("climb") CLIMB,
    @JsonProperty("fly") FLY,
    @JsonProperty("swim") SWIM,
    @JsonProperty("burrow") BURROW
}
