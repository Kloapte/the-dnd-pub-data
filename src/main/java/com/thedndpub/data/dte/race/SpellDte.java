package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.SpellKnownDteDeserializer;
import lombok.Data;

@Data
public class SpellDte {
    @JsonProperty("_")
    @JsonDeserialize(using = SpellKnownDteDeserializer.class)
    private LevelSpellKnownDte levelUnknown;

    @JsonProperty("1")
    @JsonDeserialize(using = SpellKnownDteDeserializer.class)
    private LevelSpellKnownDte level1;

    @JsonProperty("3")
    @JsonDeserialize(using = SpellKnownDteDeserializer.class)
    private LevelSpellKnownDte level3;

    @JsonProperty("5")
    @JsonDeserialize(using = SpellKnownDteDeserializer.class)
    private LevelSpellKnownDte level5;
}
