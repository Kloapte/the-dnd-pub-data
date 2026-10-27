package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleBurrowingMaterial {
    @JsonProperty("sand") SAND,
    @JsonProperty("earth") EARTH,
    @JsonProperty("mud") MUD,
    @JsonProperty("ice") ICE
}
