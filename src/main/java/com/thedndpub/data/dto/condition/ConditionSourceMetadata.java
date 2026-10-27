package com.thedndpub.data.dto.condition;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionSourceMetadata(
        String source,
        Integer page,
        ConditionSourceFlags flags,
        List<ConditionSourceCitation> citations,
        List<ConditionReference> reprintedAs) {

    public ConditionSourceMetadata {
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
