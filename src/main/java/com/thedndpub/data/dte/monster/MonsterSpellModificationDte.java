package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSpellModificationDeserializer.class)
public sealed interface MonsterSpellModificationDte permits MonsterSpellModificationTextDte,
        MonsterSpellModificationEntryDte, MonsterSpellReplacementDte {
}
