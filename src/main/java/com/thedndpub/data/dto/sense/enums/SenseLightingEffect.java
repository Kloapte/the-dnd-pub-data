package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseLightingEffect {
    @JsonProperty("dim-light") DIM_LIGHT,
    @JsonProperty("bright-light") BRIGHT_LIGHT,
    @JsonProperty("no-benefit") NO_BENEFIT
}
