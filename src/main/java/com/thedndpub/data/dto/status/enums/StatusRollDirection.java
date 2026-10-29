package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusRollDirection {
    @JsonProperty("advantage") ADVANTAGE,
    @JsonProperty("disadvantage") DISADVANTAGE
}
