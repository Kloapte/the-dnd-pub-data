package com.thedndpub.data.dto.race;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RaceGrantChoiceDto(
        Integer count,
        List<String> from,
        List<String> categories,
        String fromFilter) {
    public RaceGrantChoiceDto {
        from = from == null ? List.of() : List.copyOf(from);
        categories = categories == null ? List.of() : List.copyOf(categories);
    }
}
