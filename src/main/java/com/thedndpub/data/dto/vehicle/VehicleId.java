package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleId(String value) {
    public VehicleId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Vehicle ID must not be blank");
        }
    }
}
