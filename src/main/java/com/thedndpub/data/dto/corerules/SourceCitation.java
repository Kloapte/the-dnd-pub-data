package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SourceCitation(String source, Integer page) {
    public SourceCitation {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Citation source is required");
        }
        if (page == null || page < 1) {
            throw new IllegalArgumentException("Citation page must be positive");
        }
    }
}
