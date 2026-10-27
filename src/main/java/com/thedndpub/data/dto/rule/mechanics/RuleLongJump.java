package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleLongJump(String kind, RuleJumpDistanceFormula distanceFormula, Integer runningStartFeet,
        Double standingJumpMultiplier, Integer movementCostPerFoot, RuleJumpLandingCheck landingCheck,
        RuleJumpObstacleCheck obstacleCheck) implements RuleMovementDetails { }
