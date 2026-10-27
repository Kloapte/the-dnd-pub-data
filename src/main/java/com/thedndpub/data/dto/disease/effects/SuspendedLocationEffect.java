package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SuspendedLocationEffect(
        DiseaseEffectKind kind,
        Double heightFeet,
        String lifecycle) implements DiseaseEffect {
}
