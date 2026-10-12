package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterPassiveDeserializer.class)
public sealed interface MonsterPassiveDte permits MonsterPassiveValueDte, MonsterPassiveTextDte {
}
