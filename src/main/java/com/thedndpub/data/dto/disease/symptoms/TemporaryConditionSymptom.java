package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TemporaryConditionSymptom(
        String kind,
        String activation,
        String symptomId,
        DiseaseReference condition,
        DiseaseInterval duration,
        DiseaseSavingThrowRepeat repeatSaveToEnd,
        DiseaseInterval automaticSuccessAfter) implements DiseaseSymptom {
}
