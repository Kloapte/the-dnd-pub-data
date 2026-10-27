package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestHourUnit;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestInterruptionDuration(Double amount, RuleRestHourUnit unit) {
}
