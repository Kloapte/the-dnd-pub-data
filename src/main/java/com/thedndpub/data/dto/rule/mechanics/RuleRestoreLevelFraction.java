package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestoreLevelFraction(String kind, String resource, String basis, Integer minimum,
        Integer fractionDenominator, String rounding) implements RuleRecoveryOutcome { }
