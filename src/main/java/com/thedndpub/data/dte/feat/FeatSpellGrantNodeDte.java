package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = FeatSpellGrantNodeDeserializer.class)
public sealed interface FeatSpellGrantNodeDte permits FeatSpellTextNodeDte, FeatSpellArrayNodeDte,
        FeatSpellObjectNodeDte, FeatSpellCountNodeDte, FeatSpellFlagNodeDte {
}
