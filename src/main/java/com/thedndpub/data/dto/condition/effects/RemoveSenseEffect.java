package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionActor;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;
import com.thedndpub.data.dto.condition.enums.ConditionSense;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RemoveSenseEffect(ConditionEffectKind kind, ConditionSense sense, ConditionActor actor)
        implements ConditionEffect {
}
