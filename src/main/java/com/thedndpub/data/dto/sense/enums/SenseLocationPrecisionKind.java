package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseLocationPrecisionKind {
    @JsonProperty("VIBRATION_ORIGIN") VIBRATION_ORIGIN,
    @JsonProperty("DETECTED_TARGETS") DETECTED_TARGETS
}
