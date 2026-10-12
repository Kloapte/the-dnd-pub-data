package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterCopyModifierManyDte(List<MonsterCopyModifierDte> modifiers) implements MonsterCopyModifierSetDte {
}
