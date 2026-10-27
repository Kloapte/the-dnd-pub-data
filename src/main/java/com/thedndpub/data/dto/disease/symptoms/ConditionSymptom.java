package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionSymptom(String kind, DiseaseReference condition) implements DiseaseSymptom {
}
