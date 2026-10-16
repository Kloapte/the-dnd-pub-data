package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceHeightAndWeightDto(
        Integer baseHeight,
        String heightModifier,
        Integer baseWeight,
        String weightModifier) {
}
