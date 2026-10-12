package com.thedndpub.data.dte.monster;

import java.util.List;

public record MonsterCopyFeatureListPayloadDte(
        List<MonsterFeatureDte> features) implements MonsterCopyModifierPayloadDte {
}
