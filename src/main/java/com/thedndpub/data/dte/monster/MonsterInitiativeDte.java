package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterInitiativeDeserializer.class)
public sealed interface MonsterInitiativeDte permits MonsterInitiativeValueDte, MonsterInitiativeDetailsDte {
}
