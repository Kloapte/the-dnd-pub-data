package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleDowntimeDurationUnit {
    @JsonProperty("day") DAY,
    @JsonProperty("week") WEEK,
    @JsonProperty("month") MONTH
}
