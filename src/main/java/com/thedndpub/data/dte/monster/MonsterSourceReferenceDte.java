package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = MonsterSourceReferenceDeserializer.class)
public sealed interface MonsterSourceReferenceDte permits MonsterSourceTextDte, MonsterSourceDetailsDte {
}
