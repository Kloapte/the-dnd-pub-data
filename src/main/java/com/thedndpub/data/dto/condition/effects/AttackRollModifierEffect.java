package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;
import com.thedndpub.data.dto.condition.enums.ConditionEffectDuration;
import com.thedndpub.data.dto.condition.enums.ConditionModifierDirection;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttackRollModifierEffect(
        ConditionEffectKind kind,
        String roll,
        ConditionModifierDirection direction,
        ConditionActor actor,
        ConditionActor target,
        String appliesWhen,
        Integer minimumLevel,
        ConditionEffectDuration duration,
        ConditionApplicability applicability) implements ConditionEffect {
}
