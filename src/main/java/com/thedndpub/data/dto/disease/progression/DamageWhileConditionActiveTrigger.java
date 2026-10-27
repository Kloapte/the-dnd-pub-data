package com.thedndpub.data.dto.disease.progression;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageWhileConditionActiveTrigger(String kind, String damageType, DiseaseReference condition)
        implements DiseaseProgressionTrigger {
}
