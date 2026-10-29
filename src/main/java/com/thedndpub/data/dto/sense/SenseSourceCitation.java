package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseSourceCitation(String source, Integer page) {
    public SenseSourceCitation {
        Objects.requireNonNull(source, "Citation source is required");
        if (source.isEmpty()) throw new IllegalArgumentException("Citation source must not be empty");
        Objects.requireNonNull(page, "Citation page is required");
        if (page < 1) throw new IllegalArgumentException("Citation page must be positive");
    }
}
