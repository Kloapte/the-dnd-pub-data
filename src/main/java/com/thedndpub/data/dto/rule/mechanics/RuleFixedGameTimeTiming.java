package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleGameTimeUnit;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFixedGameTimeTiming(
        RuleTurnTimingKind kind,
        Double amount,
        RuleGameTimeUnit unit) implements RuleTurnTiming {
    public RuleFixedGameTimeTiming {
        if (kind != RuleTurnTimingKind.FIXED_GAMETIME) {
            throw new IllegalArgumentException("Fixed game-time timing kind is required");
        }
        Objects.requireNonNull(amount, "Fixed game-time amount is required");
        if (amount <= 0) throw new IllegalArgumentException("Fixed game-time amount must be positive");
        Objects.requireNonNull(unit, "Fixed game-time unit is required");
    }
}
