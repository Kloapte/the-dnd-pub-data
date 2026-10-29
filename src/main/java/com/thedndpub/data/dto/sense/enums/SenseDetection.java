package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseDetection {
    @JsonProperty("creatures") CREATURES,
    @JsonProperty("movement") MOVEMENT,
    @JsonProperty("objects") OBJECTS
}
