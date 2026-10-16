package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RaceVersionAbstractDte(
        String name,
        String source,
        @JsonProperty("_mod") RaceModificationDte modification) {
}
