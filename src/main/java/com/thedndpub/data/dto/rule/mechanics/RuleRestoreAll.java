package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRecoveryResource;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestoreAll(String kind, RuleRecoveryResource resource) implements RuleRecoveryOutcome { }
