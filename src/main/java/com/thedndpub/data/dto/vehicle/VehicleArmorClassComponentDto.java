package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleArmorClassComponentDto(Integer value, List<String> factors) {
    public VehicleArmorClassComponentDto {
        factors = factors == null ? List.of() : List.copyOf(factors);
    }
}
