package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSpendResourceToRecover(String kind, String resource, String formula, Integer minimumPerDie,
        Boolean additionalChoiceAfterEachRoll) implements RuleRecoveryOutcome { }
