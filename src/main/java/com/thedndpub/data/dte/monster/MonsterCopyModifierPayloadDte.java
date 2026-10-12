package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterCopyModifierPayloadDeserializer.class)
public sealed interface MonsterCopyModifierPayloadDte permits MonsterCopyTextPayloadDte,
        MonsterCopyTextListPayloadDte, MonsterCopyFeaturePayloadDte, MonsterCopyFeatureListPayloadDte,
        MonsterCopySpellcastingPayloadDte, MonsterCopySpellcastingListPayloadDte, MonsterCopyDefensePayloadDte {
}
