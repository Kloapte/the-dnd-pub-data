package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpeedChangeEffect(
        DiseaseEffectKind kind,
        Double amount,
        String unit,
        String lifecycle,
        DiseaseInterval interval,
        Double threshold) implements DiseaseEffect {
}
