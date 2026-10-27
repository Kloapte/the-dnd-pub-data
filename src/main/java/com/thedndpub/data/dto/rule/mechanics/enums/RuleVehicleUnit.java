package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleVehicleUnit {
    @JsonProperty("feet") FEET,
    @JsonProperty("miles") MILES,
    @JsonProperty("nautical-miles") NAUTICAL_MILES
}
