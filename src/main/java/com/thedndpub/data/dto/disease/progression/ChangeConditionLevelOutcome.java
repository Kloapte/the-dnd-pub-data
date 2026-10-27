package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ChangeConditionLevelOutcome(String kind, DiseaseReference condition, Integer amount)
        implements DiseaseProgressionOutcome {
}
