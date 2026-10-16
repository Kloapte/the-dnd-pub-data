package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FeatVersionDte(
        String name,
        String source,
        @JsonProperty("_mod") FeatVersionModDte modification,
        List<FeatAdditionalSpellDte> additionalSpells) {
}
