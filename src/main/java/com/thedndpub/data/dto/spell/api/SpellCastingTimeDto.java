package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellCastingTimeDto(
        Integer amount,
        String unit,
        Boolean hasCondition,
        Boolean hasNote) {
}
