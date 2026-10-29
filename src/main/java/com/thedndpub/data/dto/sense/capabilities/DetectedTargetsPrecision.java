package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.enums.SenseDetection;
import com.thedndpub.data.dto.sense.enums.SenseLocationPrecisionKind;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DetectedTargetsPrecision(
        SenseLocationPrecisionKind kind,
        List<SenseDetection> targets) implements SenseLocationPrecision {
    public DetectedTargetsPrecision {
        if (kind != SenseLocationPrecisionKind.DETECTED_TARGETS) {
            throw new IllegalArgumentException("Detected targets kind is required");
        }
        Objects.requireNonNull(targets, "Detected targets are required");
        if (targets.isEmpty() || new HashSet<>(targets).size() != targets.size()) {
            throw new IllegalArgumentException("Detected targets must be nonempty and unique");
        }
        targets = List.copyOf(targets);
    }
}
