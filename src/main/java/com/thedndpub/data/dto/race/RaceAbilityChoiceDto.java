package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceAbilityChoiceDto(List<String> from, Integer count, Integer amount) {
    public RaceAbilityChoiceDto {
        from = from == null ? List.of() : List.copyOf(from);
    }
}
