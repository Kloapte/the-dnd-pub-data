package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SubraceCopyDte(
        String name,
        String source,
        String raceName,
        String raceSource,
        @JsonProperty("_mod") RaceModificationDte modification) {
}
