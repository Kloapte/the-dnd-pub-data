package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.race.deserializers.SpellAbilityDeserializer;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;

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
