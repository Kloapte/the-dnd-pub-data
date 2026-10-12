package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterCopyModDeserializer.class)
public record MonsterCopyModDte(java.util.Map<String, MonsterCopyModifierSetDte> changes) {
}
