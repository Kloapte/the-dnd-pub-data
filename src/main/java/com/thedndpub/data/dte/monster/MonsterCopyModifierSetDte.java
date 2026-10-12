package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterCopyModifierSetDeserializer.class)
public sealed interface MonsterCopyModifierSetDte permits MonsterCopyModifierOneDte, MonsterCopyModifierManyDte, MonsterCopyModifierTextDte {
}
