package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ItemTypeCopyDte(String abbreviation, String source) {
    public ItemTypeCopyDte(
            @JsonProperty("abbreviation") String abbreviation,
            @JsonProperty("source") String source) {
        this.abbreviation = abbreviation;
        this.source = source;
    }
}
