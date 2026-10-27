package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSourceMetadata(
        String source,
        Integer page,
        DiseaseSourceFlags flags,
        List<DiseaseSourceCitation> citations,
        List<DiseaseReference> reprintedAs) {

    public DiseaseSourceMetadata {
        citations = citations == null ? List.of() : List.copyOf(citations);
        reprintedAs = reprintedAs == null ? List.of() : List.copyOf(reprintedAs);
    }
}
