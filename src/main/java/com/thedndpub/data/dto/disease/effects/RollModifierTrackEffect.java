package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RollModifierTrackEffect(
        DiseaseEffectKind kind,
        List<String> rolls,
        Double amount,
        String requiresSense,
        String trigger,
        Double step,
        Double threshold,
        DiseaseReference appliesConditionAtThreshold,
        Boolean startsAfterSymptomOnset,
        String conditionUntil) implements DiseaseEffect {
    public RollModifierTrackEffect {
        rolls = rolls == null ? List.of() : List.copyOf(rolls);
    }
}
