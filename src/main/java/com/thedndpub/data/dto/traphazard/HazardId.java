package com.thedndpub.data.dto.traphazard;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HazardId(String value) {
    public HazardId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hazard ID must not be blank");
        }
    }
}
