package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Monster feature content is ordinary shared entry prose or a dedicated spellcasting block.
 */
@JsonDeserialize(using = MonsterEntryDeserializer.class)
public sealed interface MonsterEntryDte permits SharedMonsterEntryDte, SpellcastingMonsterEntryDte {
}
