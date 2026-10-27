package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExtraFeetPerFootCost(String kind, Integer additionalFeetPerFoot) implements ConditionMovementCost {
}
