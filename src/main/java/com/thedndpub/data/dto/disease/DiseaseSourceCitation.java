package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSourceCitation(String source, Integer page) {
}
