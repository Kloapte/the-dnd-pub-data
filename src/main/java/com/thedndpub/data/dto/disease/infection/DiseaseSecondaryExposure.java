package com.thedndpub.data.dto.disease.infection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseSecondaryExposure(
        String exposureId,
        String kind,
        String trigger,
        String symptomId,
        Double distance,
        String unit,
        DiseaseSavingThrow savingThrow,
        DiseaseSecondaryExposureFailure onFailure,
        DiseaseSecondaryExposureSuccess onSuccess) {
}
