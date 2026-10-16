package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemVehicleStatisticsDto(
        Integer crew,
        Integer crewMinimum,
        Integer crewMaximum,
        Integer armorClass,
        Integer hitPoints,
        Integer damageThreshold,
        Integer speed,
        Integer passengerCapacity,
        Integer cargoCapacity,
        Integer carryingCapacity,
        Integer travelCost,
        Integer shippingCost,
        ItemBarDimensionsDto barDimensions) {
}
