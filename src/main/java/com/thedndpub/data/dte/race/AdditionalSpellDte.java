package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.race.deserializers.SpellAbilityDeserializer;
import lombok.Data;

@Data
public class AdditionalSpellDte {
    private String raceName;
    private String name;
    private SpellDte innate;
    @JsonDeserialize(using = SpellAbilityDeserializer.class)
    private SpellAbilityDte ability;
    private SpellDte known;
    private Object expanded;
}
