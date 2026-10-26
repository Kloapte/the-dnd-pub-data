package com.thedndpub.data.dto.corerules.sense;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NonvisualPerception(
        String kind,
        String medium,
        List<String> detects,
        List<String> restrictions,
        LocationPrecision locationPrecision) implements SenseCapability {
    public NonvisualPerception {
        if (!"NONVISUAL_PERCEPTION".equals(kind) || medium == null) {
            throw new IllegalArgumentException("Invalid nonvisual perception capability");
        }
        detects = detects == null ? null : List.copyOf(detects);
        restrictions = restrictions == null ? null : List.copyOf(restrictions);
    }
}
