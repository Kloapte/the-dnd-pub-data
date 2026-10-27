package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleEncumbranceState;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RuleEncumbranceThreshold(Double weightMultiplier, RuleEncumbranceState state, Double speedReductionFeet,
        List<ActionAbility> abilityCheckDisadvantage, List<ActionAbility> attackDisadvantage,
        List<ActionAbility> savingThrowDisadvantage) {
    public RuleEncumbranceThreshold {
        abilityCheckDisadvantage = abilityCheckDisadvantage == null ? List.of() : List.copyOf(abilityCheckDisadvantage);
        attackDisadvantage = attackDisadvantage == null ? List.of() : List.copyOf(attackDisadvantage);
        savingThrowDisadvantage = savingThrowDisadvantage == null ? List.of() : List.copyOf(savingThrowDisadvantage);
    }
}
