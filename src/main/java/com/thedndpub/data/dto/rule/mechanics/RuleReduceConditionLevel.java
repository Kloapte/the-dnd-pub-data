package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleReduceConditionLevel(String kind, RuleConditionReference condition, Integer levels)
        implements RuleRecoveryOutcome { }
