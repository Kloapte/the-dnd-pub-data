package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatAbilityChoiceDto(List<String> from, Integer amount, Integer count) {
    public FeatAbilityChoiceDto {
        from = from == null ? List.of() : List.copyOf(from);
    }
}
