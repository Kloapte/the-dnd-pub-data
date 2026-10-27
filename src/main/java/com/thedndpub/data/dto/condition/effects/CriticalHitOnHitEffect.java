package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.ConditionReference;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CriticalHitOnHitEffect(
        ConditionEffectKind kind,
        String attacker,
        String target,
        Integer distanceFeet,
        Boolean requiresSuccessfulHit,
        String result,
        ConditionReference rule) implements ConditionEffect {
}
