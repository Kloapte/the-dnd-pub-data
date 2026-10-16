package com.thedndpub.data.dto.feat;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FeatSpellObjectDto(Map<String, FeatSpellGrantNodeDto> values) implements FeatSpellGrantNodeDto {
    public FeatSpellObjectDto {
        values = Collections.unmodifiableMap(new TreeMap<>(values));
    }
}
