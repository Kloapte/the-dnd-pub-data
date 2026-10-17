package com.thedndpub.data.dte.deity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DeitiesFileDte(
        @JsonProperty("_meta") DeitiesMetadataDte metadata,
        List<DeityDte> deity) {
    public DeitiesFileDte {
        deity = deity == null ? List.of() : List.copyOf(deity);
    }
}
