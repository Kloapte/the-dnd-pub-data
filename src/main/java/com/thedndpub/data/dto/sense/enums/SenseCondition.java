package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseCondition {
    @JsonProperty("darkness") DARKNESS,
    @JsonProperty("heavily-obscured") HEAVILY_OBSCURED,
    @JsonProperty("lightly-obscured") LIGHTLY_OBSCURED
}
