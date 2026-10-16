package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RaceTemplateVariablesDte(
        String color,
        String damageType,
        String area,
        String savingThrow,
        @JsonProperty("resist") List<String> resistance) {
    public RaceTemplateVariablesDte {
        resistance = resistance == null ? null : List.copyOf(resistance);
    }
}
