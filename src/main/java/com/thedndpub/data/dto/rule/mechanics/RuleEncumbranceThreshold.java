package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RuleEncumbranceThreshold(Double weightMultiplier, String state, Double speedReductionFeet,
        List<String> abilityCheckDisadvantage, List<String> attackDisadvantage,
        List<String> savingThrowDisadvantage) {
    public RuleEncumbranceThreshold {
        abilityCheckDisadvantage = abilityCheckDisadvantage == null ? List.of() : List.copyOf(abilityCheckDisadvantage);
        attackDisadvantage = attackDisadvantage == null ? List.of() : List.copyOf(attackDisadvantage);
        savingThrowDisadvantage = savingThrowDisadvantage == null ? List.of() : List.copyOf(savingThrowDisadvantage);
    }
}
