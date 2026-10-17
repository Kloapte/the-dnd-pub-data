package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleUpgradeId(String value) {
    public VehicleUpgradeId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Vehicle Upgrade ID must not be blank");
        }
    }
}
