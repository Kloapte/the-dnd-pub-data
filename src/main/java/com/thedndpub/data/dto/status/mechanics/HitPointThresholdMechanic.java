package com.thedndpub.data.dto.status.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.status.StatusReference;
import com.thedndpub.data.dto.status.enums.StatusMechanicKind;
import com.thedndpub.data.dto.status.enums.StatusThresholdComparison;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HitPointThresholdMechanic(
        StatusMechanicKind kind,
        Double fraction,
        StatusThresholdComparison comparison,
        StatusReference reference) implements StatusMechanic {
    public HitPointThresholdMechanic {
        if (kind != StatusMechanicKind.HIT_POINT_THRESHOLD) {
            throw new IllegalArgumentException("Hit point threshold kind is invalid");
        }
        Objects.requireNonNull(fraction, "Hit point threshold fraction is required");
        if (!Double.isFinite(fraction) || fraction <= 0 || fraction > 1) {
            throw new IllegalArgumentException("Hit point threshold fraction must be greater than zero and at most one");
        }
        Objects.requireNonNull(comparison, "Hit point threshold comparison is required");
    }
}
