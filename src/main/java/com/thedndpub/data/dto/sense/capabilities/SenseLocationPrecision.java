package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VibrationOriginPrecision.class, name = "VIBRATION_ORIGIN"),
        @JsonSubTypes.Type(value = DetectedTargetsPrecision.class, name = "DETECTED_TARGETS")
})
public sealed interface SenseLocationPrecision permits VibrationOriginPrecision, DetectedTargetsPrecision {
}
