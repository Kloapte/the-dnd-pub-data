package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleStationDto(
        String name,
        Integer armorClass,
        Integer hitPoints,
        Integer crew,
        List<String> size) {
    public VehicleStationDto {
        size = size == null ? List.of() : List.copyOf(size);
    }
}
