package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundAbilityChoiceDto(List<String> abilities, List<Integer> weights) {
    public BackgroundAbilityChoiceDto {
        abilities = List.copyOf(abilities);
        weights = List.copyOf(weights);
    }
}
