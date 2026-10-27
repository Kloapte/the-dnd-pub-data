package com.thedndpub.data.dto.rule.mechanics;

import com.thedndpub.data.dto.rule.mechanics.deserializers.RuleTurnTimingDeserializer;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;
import tools.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RuleTurnTimingDeserializer.class)
public sealed interface RuleTurnTiming permits RuleActionTiming, RuleReactionTiming, RuleFixedGameTimeTiming,
        RuleVariedGameTimeTiming, RuleAlternativeTiming {
    RuleTurnTimingKind kind();
}
