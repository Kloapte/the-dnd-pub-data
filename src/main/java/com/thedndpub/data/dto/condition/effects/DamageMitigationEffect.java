package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageMitigationEffect(
        ConditionEffectKind kind,
        String damageTypes,
        String mitigation) implements ConditionEffect {
}
