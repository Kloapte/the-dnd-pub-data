package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleActionTiming(RuleTurnTimingKind kind) implements RuleTurnTiming {
    public RuleActionTiming {
        if (kind != RuleTurnTimingKind.ACTION && kind != RuleTurnTimingKind.BONUS_ACTION
                && kind != RuleTurnTimingKind.FREE) {
            throw new IllegalArgumentException("Action timing kind must be action, bonus-action, or free");
        }
    }
}
