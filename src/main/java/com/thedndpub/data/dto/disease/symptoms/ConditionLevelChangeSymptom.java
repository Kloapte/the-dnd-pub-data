package com.thedndpub.data.dto.disease.symptoms;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionLevelChangeSymptom(
        String kind,
        DiseaseReference condition,
        Integer amount,
        Boolean cannotReduceUntilDiseaseEnds,
        Boolean onlyProgressionBranchCanReduce) implements DiseaseSymptom {
}
