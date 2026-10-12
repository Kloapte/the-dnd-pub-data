package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterDefensesDte(List<MonsterDefenseDte> resist, List<MonsterDefenseDte> immune,
                                 List<MonsterDefenseDte> vulnerable, List<MonsterDefenseDte> conditionImmune) {
}
