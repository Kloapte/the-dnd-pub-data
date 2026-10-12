package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterAlternateSpeedDte(List<MonsterSpeedAlternateModeDte> walk,
                                       List<MonsterSpeedAlternateModeDte> burrow,
                                       List<MonsterSpeedAlternateModeDte> climb,
                                       List<MonsterSpeedAlternateModeDte> fly,
                                       List<MonsterSpeedAlternateModeDte> swim) {
}
