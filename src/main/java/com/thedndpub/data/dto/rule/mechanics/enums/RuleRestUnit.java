package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleRestUnit {
    @JsonProperty("minute") MINUTE,
    @JsonProperty("minutes") MINUTES,
    @JsonProperty("hour") HOUR,
    @JsonProperty("hours") HOURS,
    @JsonProperty("day") DAY,
    @JsonProperty("days") DAYS,
    @JsonProperty("week") WEEK,
    @JsonProperty("weeks") WEEKS
}
