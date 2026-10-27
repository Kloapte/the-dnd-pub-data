package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;
import com.thedndpub.data.dto.condition.enums.ConditionEffectDuration;
import com.thedndpub.data.dto.condition.enums.ConditionModifierDirection;
import com.thedndpub.data.dto.condition.enums.ConditionRollType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CheckRollModifierEffect(
        ConditionEffectKind kind,
        ConditionRollType roll,
        ConditionModifierDirection direction,
        ConditionActor actor,
        String ability,
        Double amountPerLevel,
        ConditionEffectDuration duration,
        ConditionApplicability applicability,
        Integer minimumLevel) implements ConditionEffect {
}
