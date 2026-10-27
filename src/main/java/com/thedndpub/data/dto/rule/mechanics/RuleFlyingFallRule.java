package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlyingFallRule(String kind, String ruleStatus, List<String> fallingTriggers, String prevention)
        implements RuleMovementDetails {
    public RuleFlyingFallRule { fallingTriggers = fallingTriggers == null ? List.of() : List.copyOf(fallingTriggers); }
}
