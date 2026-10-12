package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ConsumeDeserializer.class)
public sealed interface ConsumeDte permits ConsumeFlagDte, ConsumeNoteDte {
}
