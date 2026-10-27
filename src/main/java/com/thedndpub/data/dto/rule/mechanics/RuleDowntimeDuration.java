package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDowntimeDurationUnit;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDowntimeDuration(Double amount, RuleDowntimeDurationUnit unit) {
}
