package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionTrigger;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleReactionTiming(RuleTurnTimingKind kind, ActionTrigger trigger) implements RuleTurnTiming {
    public RuleReactionTiming {
        if (kind != RuleTurnTimingKind.REACTION) throw new IllegalArgumentException("Reaction timing kind is required");
        Objects.requireNonNull(trigger, "Reaction timing requires an action trigger");
    }
}
