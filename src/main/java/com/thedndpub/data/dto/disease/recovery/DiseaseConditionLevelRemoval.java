package com.thedndpub.data.dto.disease.recovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseConditionLevelRemoval(DiseaseReference condition, String scope) {
}
