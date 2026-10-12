package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterCopyTextListPayloadDte(List<String> values) implements MonsterCopyModifierPayloadDte {
}
