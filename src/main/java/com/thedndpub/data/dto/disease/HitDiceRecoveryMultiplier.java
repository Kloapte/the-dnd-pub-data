package com.thedndpub.data.dto.disease;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HitDiceRecoveryMultiplier(
        String kind,
        DiseaseReference condition,
        Integer minimumLevel,
        Double multiplier) implements DiseaseConditionGatedRestriction {
}
