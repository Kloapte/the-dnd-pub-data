package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSourceMetadata(
        String source,
        Integer page,
        ActionSourceFlags flags,
        List<ActionSourceCitation> citations,
        List<ActionReference> reprintedAs) {

    public ActionSourceMetadata {
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
