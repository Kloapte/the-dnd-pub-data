package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionMovementUnit {
    @JsonProperty("feet") FEET,
    @JsonProperty("speed-fraction") SPEED_FRACTION
}
