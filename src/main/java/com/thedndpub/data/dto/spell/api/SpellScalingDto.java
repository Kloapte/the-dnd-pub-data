package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellScalingDto(String label, List<SpellScalingValueDto> values) {
    public SpellScalingDto {
        values = List.copyOf(values);
    }
}
