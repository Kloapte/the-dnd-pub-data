package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFailedSaveOutcomeApplyCondition(String kind, RuleConditionReference condition)
        implements RuleFailedSaveOutcome { }
