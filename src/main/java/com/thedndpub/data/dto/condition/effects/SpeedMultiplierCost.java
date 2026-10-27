package com.thedndpub.data.dto.condition.effects;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpeedMultiplierCost(String kind, Double multiplier) implements ConditionMovementCost {
}
