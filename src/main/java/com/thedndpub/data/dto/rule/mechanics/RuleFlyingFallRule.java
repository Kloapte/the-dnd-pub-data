package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlyingFallPrevention;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlyingFallRuleStatus;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleFlyingFallTrigger;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlyingFallRule(String kind, RuleFlyingFallRuleStatus ruleStatus,
        List<RuleFlyingFallTrigger> fallingTriggers, RuleFlyingFallPrevention prevention)
        implements RuleMovementDetails {
    public RuleFlyingFallRule {
        fallingTriggers = fallingTriggers == null ? List.of() : List.copyOf(fallingTriggers);
    }
}
