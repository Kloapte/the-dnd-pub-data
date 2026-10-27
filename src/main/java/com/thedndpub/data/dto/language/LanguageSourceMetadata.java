package com.thedndpub.data.dto.language;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LanguageSourceMetadata(
        String source,
        Integer page,
        LanguageSourceFlags flags,
        List<LanguageSourceCitation> citations,
        List<LanguageReference> reprintedAs) {
    public LanguageSourceMetadata {
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
