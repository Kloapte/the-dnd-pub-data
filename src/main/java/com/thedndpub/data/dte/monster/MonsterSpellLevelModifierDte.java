package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSpellLevelModifierDeserializer.class)
public sealed interface MonsterSpellLevelModifierDte permits MonsterSpellLevelModifierValueDte,
        MonsterSpellLevelModifierListDte {
}
