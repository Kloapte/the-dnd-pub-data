package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellDurationDto(
        String type,
        Boolean concentration,
        SpellDurationAmountDto duration,
        List<String> ends) {
    public SpellDurationDto {
        ends = List.copyOf(ends);
    }
}
