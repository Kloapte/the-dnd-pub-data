package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.condition.enums.ConditionEffectKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PhysicalStateEffect(
        ConditionEffectKind kind,
        String state,
        Boolean includesWornAndCarriedNonmagicalObjects,
        Integer weightMultiplier,
        String aging) implements ConditionEffect {
}
