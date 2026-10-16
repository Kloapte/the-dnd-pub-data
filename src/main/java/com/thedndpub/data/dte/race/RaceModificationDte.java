package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonDeserialize(using = RaceModificationDeserializer.class)
public record RaceModificationDte(List<RaceEntryModificationDte> entries) {
    public RaceModificationDte {
        entries = entries == null ? List.of() : List.copyOf(entries);
    }
}
