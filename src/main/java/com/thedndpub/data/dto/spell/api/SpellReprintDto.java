package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellReprintDto(String id, String name, SourceReference source) {
}
