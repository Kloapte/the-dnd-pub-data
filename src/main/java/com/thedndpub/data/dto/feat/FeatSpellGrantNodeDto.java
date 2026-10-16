package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FeatSpellTextDto.class, name = "text"),
        @JsonSubTypes.Type(value = FeatSpellArrayDto.class, name = "array"),
        @JsonSubTypes.Type(value = FeatSpellObjectDto.class, name = "object"),
        @JsonSubTypes.Type(value = FeatSpellCountDto.class, name = "count"),
        @JsonSubTypes.Type(value = FeatSpellFlagDto.class, name = "flag")
})
public sealed interface FeatSpellGrantNodeDto permits FeatSpellTextDto, FeatSpellArrayDto,
        FeatSpellObjectDto, FeatSpellCountDto, FeatSpellFlagDto {
}
