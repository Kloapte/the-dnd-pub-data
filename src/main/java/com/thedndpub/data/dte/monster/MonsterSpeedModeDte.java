package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSpeedModeDeserializer.class)
public sealed interface MonsterSpeedModeDte permits MonsterSpeedValueDte, MonsterSpeedDetailsDte {
}
