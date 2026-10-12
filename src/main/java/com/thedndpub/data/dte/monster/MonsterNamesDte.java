package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterNamesDeserializer.class)
public sealed interface MonsterNamesDte permits MonsterNameDte, MonsterNameListDte {
}
