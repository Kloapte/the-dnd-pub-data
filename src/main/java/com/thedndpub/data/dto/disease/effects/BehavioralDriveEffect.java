package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseInterval;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BehavioralDriveEffect(
        DiseaseEffectKind kind,
        String drive,
        String lifecycle,
        DiseaseInterval beginsAfterInfection) implements DiseaseEffect {
}
