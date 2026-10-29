package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseCapabilityKind {
    @JsonProperty("SIGHT") SIGHT,
    @JsonProperty("NONVISUAL_PERCEPTION") NONVISUAL_PERCEPTION,
    @JsonProperty("CONDITION_INTERACTION") CONDITION_INTERACTION
}
