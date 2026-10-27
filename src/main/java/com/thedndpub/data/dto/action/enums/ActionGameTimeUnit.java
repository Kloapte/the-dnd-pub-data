package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionGameTimeUnit {
    @JsonProperty("minute") MINUTE,
    @JsonProperty("hour") HOUR,
    @JsonProperty("day") DAY
}
