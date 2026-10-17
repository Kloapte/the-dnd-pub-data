package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleMovementComponentDto(String name, Integer armorClass, Integer hitPoints, Boolean control) {
}
