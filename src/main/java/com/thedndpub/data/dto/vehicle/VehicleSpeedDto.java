package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleSpeedDto(
        Integer value,
        Integer walk,
        VehicleSpeedModeDto fly,
        VehicleSpeedModeDto swim,
        Boolean canHover) {
}
