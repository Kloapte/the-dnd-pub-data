package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleArmorClassDto(Integer value, List<VehicleArmorClassComponentDto> components) {
    public VehicleArmorClassDto {
        components = components == null ? List.of() : List.copyOf(components);
    }
}
