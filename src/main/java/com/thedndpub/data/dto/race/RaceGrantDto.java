package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceGrantDto(
        String name,
        Boolean granted,
        Integer count,
        RaceGrantChoiceDto choice) {
}
