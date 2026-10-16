package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RacesFileDte(
        @JsonProperty("_meta") RaceMetadataDte metadata,
        List<RaceDte> race,
        List<SubraceDte> subrace) {
    public RacesFileDte {
        race = race == null ? List.of() : List.copyOf(race);
        subrace = subrace == null ? List.of() : List.copyOf(subrace);
    }
}
