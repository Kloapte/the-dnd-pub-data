package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeatSpellAbilityDeserializer.class)
public sealed interface FeatSpellAbilityDte permits FeatSpellAbilityNameDte, FeatSpellAbilityChoiceDte {
}
