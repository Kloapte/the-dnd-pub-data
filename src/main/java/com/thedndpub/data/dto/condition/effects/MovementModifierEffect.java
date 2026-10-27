package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;
import com.thedndpub.data.dto.condition.enums.ConditionEffectDuration;
import com.thedndpub.data.dto.condition.enums.ConditionMovementType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MovementModifierEffect(
        ConditionEffectKind kind,
        ConditionMovementType movement,
        ConditionActor actor,
        Double amount,
        Double amountPerLevel,
        ConditionEffectDuration duration,
        Integer minimumLevel) implements ConditionEffect {
}
