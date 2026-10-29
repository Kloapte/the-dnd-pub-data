package com.thedndpub.data.dto.sense.capabilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.sense.enums.SenseCapabilityKind;
import com.thedndpub.data.dto.sense.enums.SenseDetection;
import com.thedndpub.data.dto.sense.enums.SenseMedium;
import com.thedndpub.data.dto.sense.enums.SenseRestriction;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record NonvisualPerceptionCapability(
        SenseCapabilityKind kind,
        SenseMedium medium,
        List<SenseDetection> detects,
        List<SenseRestriction> restrictions,
        SenseLocationPrecision locationPrecision) implements SenseCapability {
    public NonvisualPerceptionCapability {
        if (kind != SenseCapabilityKind.NONVISUAL_PERCEPTION) {
            throw new IllegalArgumentException("Nonvisual perception kind is invalid");
        }
        Objects.requireNonNull(medium, "Nonvisual medium is required");
        if (detects != null && detects.isEmpty()) {
            throw new IllegalArgumentException("Nonvisual detects must not be empty when present");
        }
        detects = detects == null ? List.of() : List.copyOf(detects);
        restrictions = restrictions == null ? List.of() : List.copyOf(restrictions);
    }
}
