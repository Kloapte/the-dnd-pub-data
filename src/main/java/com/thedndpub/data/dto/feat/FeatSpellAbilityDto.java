package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatSpellAbilityDto(String ability, List<String> choices) {
    public FeatSpellAbilityDto {
        choices = choices == null ? List.of() : List.copyOf(choices);
    }
}
