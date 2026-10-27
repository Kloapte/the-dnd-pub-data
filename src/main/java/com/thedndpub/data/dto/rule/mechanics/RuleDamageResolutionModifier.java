package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDamageOperation;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDamageResolutionModifier(RuleDamageOperation operation, Double multiplier, String rounding,
        Boolean oncePerDamageInstance, Boolean sameOperationDoesNotStack) {
}
