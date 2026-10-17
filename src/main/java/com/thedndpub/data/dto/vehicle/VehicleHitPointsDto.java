package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleHitPointsDto(
        Integer value,
        Integer average,
        String formula,
        Integer hitPoints,
        Integer damageThreshold,
        Integer massiveDamageThreshold) {
}
