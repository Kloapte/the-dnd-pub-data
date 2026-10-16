package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatSpellArrayDto(List<FeatSpellGrantNodeDto> values) implements FeatSpellGrantNodeDto {
    public FeatSpellArrayDto {
        values = List.copyOf(values);
    }
}
