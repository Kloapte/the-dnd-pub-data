package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestRecoveryLimitation(String appliesWhen, RuleRecoveryOutcome limitedOutcome) {
}
