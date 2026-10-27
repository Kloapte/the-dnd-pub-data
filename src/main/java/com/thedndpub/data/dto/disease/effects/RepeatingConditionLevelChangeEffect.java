package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RepeatingConditionLevelChangeEffect(
        DiseaseEffectKind kind,
        DiseaseReference condition,
        Integer amount,
        DiseaseInterval every,
        String relativeTo) implements DiseaseEffect {
}
