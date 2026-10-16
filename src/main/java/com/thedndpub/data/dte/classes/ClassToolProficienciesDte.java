package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClassToolProficienciesDte(
        @JsonProperty("thieves' tools") Boolean thievesTools,
        @JsonProperty("tinker's tools") Boolean tinkersTools,
        @JsonProperty("herbalism kit") Boolean herbalismKit,
        Integer anyArtisansTool,
        Integer anyMusicalInstrument) {
}
