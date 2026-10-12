package com.thedndpub.data.dto.monster.identity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record Summoning(String summonedByClass, String summonedBySpell, Integer spellLevel) {
}
