package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleSurfaceCheckTrigger;
import com.thedndpub.data.dto.rule.RuleReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSurfaceMovementCheck(RuleSurfaceCheckTrigger trigger, Boolean dmMayRequire, String ability,
        RuleReference skill, Integer dc) {
}
