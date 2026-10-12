package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterShortNameDeserializer.class)
public sealed interface MonsterShortNameDte permits MonsterShortNameTextDte, MonsterShortNameFlagDte {
}
