package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRecoveryEffect;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleEndEffect(String kind, RuleRecoveryEffect effect) implements RuleRecoveryOutcome { }
