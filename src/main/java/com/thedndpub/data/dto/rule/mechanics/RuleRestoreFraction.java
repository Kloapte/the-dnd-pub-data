package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestoreFraction(String kind, String resource, Integer numerator, Integer denominator,
        Integer minimum, String rounding) implements RuleRecoveryOutcome { }
