package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDamageResolutionModifier(String operation, Double multiplier, String rounding,
        Boolean oncePerDamageInstance, Boolean sameOperationDoesNotStack) {
}
