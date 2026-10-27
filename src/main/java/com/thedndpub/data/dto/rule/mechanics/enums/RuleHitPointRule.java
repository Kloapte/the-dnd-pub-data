package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleHitPointRule {
    @JsonProperty("temporary-hit-points") TEMPORARY_HIT_POINTS,
    @JsonProperty("maximum") MAXIMUM,
    @JsonProperty("current") CURRENT,
    @JsonProperty("zero-hit-points") ZERO_HIT_POINTS
}
