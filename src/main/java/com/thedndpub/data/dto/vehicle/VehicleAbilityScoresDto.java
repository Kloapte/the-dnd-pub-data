package com.thedndpub.data.dto.vehicle;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record VehicleAbilityScoresDto(
        Integer strength,
        Integer dexterity,
        Integer constitution,
        Integer intelligence,
        Integer wisdom,
        Integer charisma) {
}
