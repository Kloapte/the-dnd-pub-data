package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterDefenseDeserializer.class)
public sealed interface MonsterDefenseDte permits MonsterDefenseNameDte, MonsterDefenseDetailsDte {
}
