package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSourceMetadata(
        String source,
        Integer page,
        RuleSourceFlags flags,
        List<RuleSourceCitation> citations,
        List<RuleReference> reprintedAs) {
    public RuleSourceMetadata {
        Objects.requireNonNull(source, "Rule sourceMetadata.source is required");
        if (source.isEmpty()) throw new IllegalArgumentException("Rule sourceMetadata.source must not be empty");
        if (page != null && page < 1) throw new IllegalArgumentException("Rule sourceMetadata.page must be positive");
        if (citations != null && citations.isEmpty()) {
            throw new IllegalArgumentException("Rule sourceMetadata.citations must not be empty when present");
        }
        if (reprintedAs != null && reprintedAs.isEmpty()) {
            throw new IllegalArgumentException("Rule sourceMetadata.reprintedAs must not be empty when present");
        }
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
