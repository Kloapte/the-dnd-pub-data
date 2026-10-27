package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AbilityRollModifierEffect(
        DiseaseEffectKind kind,
        List<String> rolls,
        String direction,
        String ability,
        String lifecycle) implements DiseaseEffect {
    public AbilityRollModifierEffect {
        rolls = rolls == null ? List.of() : List.copyOf(rolls);
    }
}
