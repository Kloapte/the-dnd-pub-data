package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.disease.DiseaseReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellCastingSaveEffect(
        DiseaseEffectKind kind,
        String component,
        String ability,
        Integer dc,
        DiseaseReference condition,
        List<String> onFailure,
        String spellSlotOutcome) implements DiseaseEffect {
    public SpellCastingSaveEffect {
        onFailure = onFailure == null ? List.of() : List.copyOf(onFailure);
    }
}
