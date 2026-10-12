package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSkillValueDeserializer.class)
public sealed interface MonsterSkillValueDte permits MonsterSkillBonusDte, MonsterSkillAlternativesDte, MonsterSkillNumericDte {
}
