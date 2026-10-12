package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterSpellLevelModifierListDte(
        List<MonsterSpellReplacementDte> values) implements MonsterSpellLevelModifierDte {
}
