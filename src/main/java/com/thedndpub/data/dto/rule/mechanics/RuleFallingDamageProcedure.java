package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFallingDamageProcedure(String kind, Integer distanceIncrementFeet, RuleDiceCount dicePerIncrement,
        Integer maximumDiceCount, String damageType, String partialIncrementTreatment,
        RuleLandingCondition landingCondition) implements RuleMovementDetails {
}
