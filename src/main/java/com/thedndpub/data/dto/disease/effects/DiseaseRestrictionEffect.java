package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiseaseRestrictionEffect(
        DiseaseEffectKind kind,
        String restriction,
        DiseaseReference condition,
        Integer minimumLevel,
        String lifecycle) implements DiseaseEffect {
}
