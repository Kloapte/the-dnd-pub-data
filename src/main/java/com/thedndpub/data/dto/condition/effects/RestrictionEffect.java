package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;
import com.thedndpub.data.dto.condition.enums.ConditionEffectDuration;
import com.thedndpub.data.dto.condition.enums.ConditionMovementUnit;
import com.thedndpub.data.dto.condition.enums.ConditionRestriction;
import com.thedndpub.data.dto.condition.enums.ConditionRestrictionTiming;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RestrictionEffect(
        ConditionEffectKind kind,
        ConditionRestriction restriction,
        ConditionActor actor,
        ConditionRestrictionTiming appliesWhen,
        Integer minimumLevel,
        ConditionEffectDuration duration,
        Double amount,
        ConditionMovementUnit unit) implements ConditionEffect {
}
