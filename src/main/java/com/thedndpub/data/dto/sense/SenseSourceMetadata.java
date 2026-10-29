package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseSourceMetadata(
        String source,
        Integer page,
        SenseSourceFlags flags,
        List<SenseSourceCitation> citations,
        List<SenseReference> reprintedAs) {
    public SenseSourceMetadata {
        Objects.requireNonNull(source, "Sense source metadata source is required");
        if (source.isEmpty()) throw new IllegalArgumentException("Sense source metadata source must not be empty");
        if (page != null && page < 1) throw new IllegalArgumentException("Sense source page must be positive");
        if (citations != null && citations.isEmpty()) {
            throw new IllegalArgumentException("Sense citations must not be empty when present");
        }
        if (reprintedAs != null && reprintedAs.isEmpty()) {
            throw new IllegalArgumentException("Sense reprintedAs must not be empty when present");
        }
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
