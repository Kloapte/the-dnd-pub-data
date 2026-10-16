package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ItemAttunementTagDte(
        @JsonProperty("class") String className,
        String background,
        Boolean spellcasting,
        String skillProficiency,
        List<String> alignment,
        String creatureType,
        String race,
        Boolean psionics,
        String languageProficiency,
        String size,
        @JsonProperty("int") Integer intScore) {
}
