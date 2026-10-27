package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleActionTiming.class, name = "action"),
        @JsonSubTypes.Type(value = RuleActionTiming.class, name = "bonus-action"),
        @JsonSubTypes.Type(value = RuleActionTiming.class, name = "free"),
        @JsonSubTypes.Type(value = RuleReactionTiming.class, name = "reaction"),
        @JsonSubTypes.Type(value = RuleFixedGameTimeTiming.class, name = "fixed_gametime"),
        @JsonSubTypes.Type(value = RuleVariedGameTimeTiming.class, name = "varied_gametime"),
        @JsonSubTypes.Type(value = RuleAlternativeTiming.class, name = "alternatives")
})
public sealed interface RuleTurnTiming permits RuleActionTiming, RuleReactionTiming, RuleFixedGameTimeTiming,
        RuleVariedGameTimeTiming, RuleAlternativeTiming {
    RuleTurnTimingKind kind();
}
