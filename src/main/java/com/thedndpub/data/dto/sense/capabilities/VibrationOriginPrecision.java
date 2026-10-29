package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.enums.SenseLocationPrecisionKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VibrationOriginPrecision(SenseLocationPrecisionKind kind) implements SenseLocationPrecision {
    public VibrationOriginPrecision {
        if (kind != SenseLocationPrecisionKind.VIBRATION_ORIGIN) {
            throw new IllegalArgumentException("Vibration origin kind is required");
        }
    }
}
