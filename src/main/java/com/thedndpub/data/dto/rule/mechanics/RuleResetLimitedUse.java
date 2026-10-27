package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestType;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleResetLimitedUse(String kind, String resource, RuleRestType restType)
        implements RuleRecoveryOutcome { }
