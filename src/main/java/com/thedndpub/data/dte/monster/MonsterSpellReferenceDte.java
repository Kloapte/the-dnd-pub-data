package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSpellReferenceDeserializer.class)
public sealed interface MonsterSpellReferenceDte permits MonsterSpellTextDte, MonsterSpellDetailsDte {
}
