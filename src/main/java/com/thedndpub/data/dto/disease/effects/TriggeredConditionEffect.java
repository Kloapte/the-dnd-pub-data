package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;
import com.thedndpub.data.dto.disease.DiseaseReference;
import com.thedndpub.data.dto.disease.DiseaseSavingThrow;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TriggeredConditionEffect(
        DiseaseEffectKind kind,
        String trigger,
        DiseaseReference condition,
        DiseaseInterval duration,
        String endsAt,
        DiseaseSavingThrow savingThrow,
        DiseaseInterval automaticSuccessAfter) implements DiseaseEffect {
}
