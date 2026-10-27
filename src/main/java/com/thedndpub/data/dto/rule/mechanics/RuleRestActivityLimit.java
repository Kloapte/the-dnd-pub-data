package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestActivity;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestHourUnit;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestActivityBound;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestActivityLimit(RuleRestActivity activity, Double amount, RuleRestHourUnit unit,
        RuleRestActivityBound limit) {
}
