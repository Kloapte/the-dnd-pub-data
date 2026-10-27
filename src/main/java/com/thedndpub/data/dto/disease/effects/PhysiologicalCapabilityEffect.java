package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PhysiologicalCapabilityEffect(
        DiseaseEffectKind kind,
        String capability,
        String lifecycle,
        String symptomId,
        DiseaseInterval beginsAfterInfection) implements DiseaseEffect {
}
