package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AutomaticCheckFailureEffect(
        ConditionEffectKind kind, String roll, String requirement, ConditionActor actor) implements ConditionEffect {
}
