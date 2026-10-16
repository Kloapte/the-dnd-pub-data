package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RaceCopyDte(
        String name,
        String source,
        @JsonProperty("_mod") RaceModificationDte modification,
        @JsonProperty("_preserve") RaceCopyPreserveDte preserve) {
}
