package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSecondaryExposureSuccess(
        String kind,
        String symptomId,
        String source,
        DiseaseInterval duration) {
}
