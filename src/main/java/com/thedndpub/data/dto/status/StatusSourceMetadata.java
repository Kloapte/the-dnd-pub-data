package com.thedndpub.data.dto.status;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StatusSourceMetadata(
        String source,
        Integer page,
        StatusSourceFlags flags,
        List<StatusSourceCitation> citations,
        List<StatusReference> reprintedAs) {
    public StatusSourceMetadata {
        Objects.requireNonNull(source, "Status source metadata source is required");
        if (source.isEmpty()) throw new IllegalArgumentException("Status source metadata source must not be empty");
        if (page != null && page < 1) throw new IllegalArgumentException("Status source page must be positive");
        if (citations != null && citations.isEmpty()) {
            throw new IllegalArgumentException("Status citations must not be empty when present");
        }
        if (reprintedAs != null && reprintedAs.isEmpty()) {
            throw new IllegalArgumentException("Status reprintedAs must not be empty when present");
        }
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
