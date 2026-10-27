package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionPredicateKind {
    @JsonProperty("HAS_CONDITION") HAS_CONDITION,
    @JsonProperty("SPEED_IS_ZERO") SPEED_IS_ZERO,
    @JsonProperty("CAN_SEE") CAN_SEE,
    @JsonProperty("DISTANCE_EXCEEDS_RANGE") DISTANCE_EXCEEDS_RANGE
}
