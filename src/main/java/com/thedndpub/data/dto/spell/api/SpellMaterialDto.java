package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellMaterialDto(Boolean required, Integer cost, Boolean consumed) {
}
