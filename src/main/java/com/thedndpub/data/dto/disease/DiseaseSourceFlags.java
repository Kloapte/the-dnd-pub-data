package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSourceFlags(Boolean srd, Boolean srd52, Boolean basicRules, Boolean basicRules2024) {
}
