package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleComponentDto(
        String name,
        Integer armorClass,
        Integer hitPoints,
        Integer damageThreshold,
        Integer crew,
        Integer count,
        List<String> size,
        List<VehicleWeaponCostDto> costs) {
    public VehicleComponentDto {
        size = size == null ? List.of() : List.copyOf(size);
        costs = costs == null ? List.of() : List.copyOf(costs);
    }
}
