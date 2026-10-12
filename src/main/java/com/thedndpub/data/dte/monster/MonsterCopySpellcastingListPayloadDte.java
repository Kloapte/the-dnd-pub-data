package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterCopySpellcastingListPayloadDte(
        List<MonsterSpellcastingDte> spellcasting) implements MonsterCopyModifierPayloadDte {
}
