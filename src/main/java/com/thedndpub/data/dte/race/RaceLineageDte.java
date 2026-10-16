package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RaceLineageDeserializer.class)
public record RaceLineageDte(Boolean flag, String description) {
}
