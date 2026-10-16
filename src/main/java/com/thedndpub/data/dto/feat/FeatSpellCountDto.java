package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatSpellCountDto(int value) implements FeatSpellGrantNodeDto {
}
