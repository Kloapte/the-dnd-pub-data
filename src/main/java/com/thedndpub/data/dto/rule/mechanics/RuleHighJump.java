package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleHighJump(String kind, RuleJumpDistanceFormula distanceFormula, Integer runningStartFeet,
        Double standingJumpMultiplier, Integer movementCostPerFoot, RuleJumpReachFormula reachFormula)
        implements RuleMovementDetails { }
