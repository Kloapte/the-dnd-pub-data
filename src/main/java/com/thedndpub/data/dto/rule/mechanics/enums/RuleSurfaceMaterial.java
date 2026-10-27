package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleSurfaceMaterial {
    @JsonProperty("heavy-snow") HEAVY_SNOW,
    @JsonProperty("ice") ICE,
    @JsonProperty("rubble") RUBBLE,
    @JsonProperty("undergrowth") UNDERGROWTH
}
