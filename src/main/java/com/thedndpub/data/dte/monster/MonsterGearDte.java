package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterGearDeserializer.class)
public sealed interface MonsterGearDte permits MonsterGearNameDte, MonsterGearDetailsDte {
}
