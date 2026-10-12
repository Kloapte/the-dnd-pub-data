package com.thedndpub.data.dto.monster.content;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.monster.spellcasting.SpellcastingBlock;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellcastingContent(SpellcastingBlock spellcasting) implements MonsterContentDto {
}
