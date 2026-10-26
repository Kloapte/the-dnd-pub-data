package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CanonicalSourceMetadata(
        String source,
        Integer page,
        SourceFlags flags,
        List<SourceCitation> citations,
        List<CanonicalReference> reprintedAs) {
    public CanonicalSourceMetadata {
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Source metadata source is required");
        }
        if (page != null && page < 1) {
            throw new IllegalArgumentException("Source metadata page must be positive");
        }
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
        if (flags != null && flags.isEmpty()) {
            flags = null;
        }
    }
}
