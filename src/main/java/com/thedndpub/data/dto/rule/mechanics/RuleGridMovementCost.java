package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleGridMovementCost(String kind, Integer gridUnitFeet, Integer movementUnitFeet,
        Integer minimumMovementRemaining, Boolean extraCostRequiresFullPayment, Integer diagonalPatternFeet,
        Boolean patternContinuesAcrossStraightMovement, Boolean ruleOptional) implements RuleMovementDetails { }
