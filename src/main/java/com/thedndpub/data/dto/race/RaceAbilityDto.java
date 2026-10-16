package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceAbilityDto(
        Integer str,
        Integer dex,
        Integer con,
        Integer intScore,
        Integer wis,
        Integer cha,
        RaceAbilityChoiceDto choice) {
}
