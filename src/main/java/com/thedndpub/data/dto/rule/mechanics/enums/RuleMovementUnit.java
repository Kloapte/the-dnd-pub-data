package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleMovementUnit {
    @JsonProperty("feet") FEET,
    @JsonProperty("speed-multiplier") SPEED_MULTIPLIER
}
