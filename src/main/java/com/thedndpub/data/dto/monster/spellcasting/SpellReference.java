package com.thedndpub.data.dto.monster.spellcasting;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellReference(String sourceText, Boolean hidden, String resolvedSpellId) {
}
