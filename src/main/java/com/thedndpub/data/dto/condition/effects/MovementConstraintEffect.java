package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.ConditionReference;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MovementConstraintEffect(
        ConditionEffectKind kind,
        ConditionActor actor,
        String constraint,
        ConditionApplicability appliesWhen,
        ConditionReference endingCondition,
        ConditionActor otherCreature,
        ConditionStandingCost standingCost) implements ConditionEffect {
}
