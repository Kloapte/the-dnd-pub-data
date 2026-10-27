package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionMovementType {
    @JsonProperty("speed-fraction") SPEED_FRACTION,
    @JsonProperty("speed-flat") SPEED_FLAT
}
