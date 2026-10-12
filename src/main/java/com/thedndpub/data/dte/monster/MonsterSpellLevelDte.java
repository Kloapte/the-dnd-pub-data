package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterSpellLevelDte(Integer slots, Integer lower, List<MonsterSpellReferenceDte> spells) {
}
