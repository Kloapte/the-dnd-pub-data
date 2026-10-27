package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRecoveryResource;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRestoreFraction(String kind, RuleRecoveryResource resource, Integer numerator, Integer denominator,
        Integer minimum, String rounding) implements RuleRecoveryOutcome { }
