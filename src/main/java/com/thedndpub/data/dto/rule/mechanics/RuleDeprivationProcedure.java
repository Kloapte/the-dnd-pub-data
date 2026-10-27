package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDeprivationProcedure(Double intervalHours, RuleDeprivationSavingThrow savingThrow,
        RuleRecoveryOutcome failureOutcome, Boolean dcResetOnLongRest) {
}
