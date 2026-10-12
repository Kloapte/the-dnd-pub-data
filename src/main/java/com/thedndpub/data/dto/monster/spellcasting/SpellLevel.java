package com.thedndpub.data.dto.monster.spellcasting;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellLevel(Integer slots, Integer lower, List<SpellReference> spells) {
}
