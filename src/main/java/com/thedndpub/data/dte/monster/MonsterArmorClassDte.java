package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterArmorClassDeserializer.class)
public sealed interface MonsterArmorClassDte permits MonsterArmorClassValueDte, MonsterArmorClassDetailsDte {
}
