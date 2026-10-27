package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RelationalRestrictionEffect(
        ConditionEffectKind kind,
        ConditionActor actor,
        ConditionActor otherCreature,
        String prohibitedOperation,
        ConditionApplicability appliesWhen) implements ConditionEffect {
}
