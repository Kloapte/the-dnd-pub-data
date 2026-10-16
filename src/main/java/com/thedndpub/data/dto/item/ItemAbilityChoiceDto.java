package com.thedndpub.data.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ItemAbilityChoiceDto(List<String> from, Integer count, Integer amount) {
    public ItemAbilityChoiceDto {
        from = List.copyOf(from);
    }
}
