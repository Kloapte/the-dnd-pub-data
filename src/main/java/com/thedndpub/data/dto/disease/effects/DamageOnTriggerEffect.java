package com.thedndpub.data.dto.disease.effects;

import com.thedndpub.data.dto.disease.enums.DiseaseEffectKind;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageOnTriggerEffect(
        DiseaseEffectKind kind,
        DiseaseDamageRoll damage,
        String trigger,
        String damageTypeRestriction) implements DiseaseEffect {
}
