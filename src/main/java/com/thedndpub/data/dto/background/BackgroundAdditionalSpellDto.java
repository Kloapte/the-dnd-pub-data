package com.thedndpub.data.dto.background;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.TreeMap;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundAdditionalSpellDto(Map<String, List<String>> expanded) {
    public BackgroundAdditionalSpellDto {
        expanded = Collections.unmodifiableMap(new TreeMap<>(expanded));
    }
}
