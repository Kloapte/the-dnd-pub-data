package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterDefenseDetailsDte(List<MonsterDefenseDte> resist, List<MonsterDefenseDte> immune,
                                       List<MonsterDefenseDte> vulnerable, List<MonsterDefenseDte> conditionImmune,
                                       Boolean cond,
                                       String note, String preNote, String postNote,
                                       String special) implements MonsterDefenseDte {
}
