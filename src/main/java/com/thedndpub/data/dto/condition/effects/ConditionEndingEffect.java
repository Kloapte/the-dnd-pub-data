package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.ConditionReference;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionEndingEffect(
        ConditionEffectKind kind,
        ConditionReference condition,
        String when) implements ConditionEffect {
}
