package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;
import com.thedndpub.data.dto.disease.DiseaseTransformationTarget;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TransformationOutcomeEffect(
        DiseaseEffectKind kind,
        DiseaseTransformationTarget target,
        String trigger,
        Boolean deathOfSubject,
        String outcomeAfter,
        Double threshold,
        DiseaseReference condition,
        String exception,
        String creatureTypeAfter,
        List<DiseaseImmunityMarker> immunities) implements DiseaseEffect {
    public TransformationOutcomeEffect {
        immunities = immunities == null ? List.of() : List.copyOf(immunities);
    }
}
