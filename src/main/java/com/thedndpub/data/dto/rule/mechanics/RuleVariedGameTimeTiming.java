package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleVariedGameTimeTiming(RuleTurnTimingKind kind) implements RuleTurnTiming {
    public RuleVariedGameTimeTiming {
        if (kind != RuleTurnTimingKind.VARIED_GAMETIME) {
            throw new IllegalArgumentException("Varied game-time timing kind is required");
        }
    }
}
