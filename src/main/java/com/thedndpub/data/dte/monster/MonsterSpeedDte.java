package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSpeedDeserializer.class)
public record MonsterSpeedDte(MonsterSpeedModeDte walk, MonsterSpeedModeDte burrow, MonsterSpeedModeDte climb,
                              MonsterSpeedModeDte fly, MonsterSpeedModeDte swim, Boolean canHover,
                              MonsterAlternateSpeedDte alternate, MonsterSpeedChoiceDte choose, String note) {
}
