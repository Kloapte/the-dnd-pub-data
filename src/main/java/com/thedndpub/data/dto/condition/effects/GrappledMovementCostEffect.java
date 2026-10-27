package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record GrappledMovementCostEffect(
        ConditionEffectKind kind,
        String actor,
        String target,
        String movementMode,
        ConditionMovementCost movementCost,
        String waiver) implements ConditionEffect {
}
