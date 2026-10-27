package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleFallingDamageProcedure.class, name = "FALL_DAMAGE"),
        @JsonSubTypes.Type(value = RuleFlyingFallRule.class, name = "FLYING_FALL_RULE"),
        @JsonSubTypes.Type(value = RuleBurrowingMovement.class, name = "BURROWING_MOVEMENT"),
        @JsonSubTypes.Type(value = RuleSurfaceMovement.class, name = "CLIMBING_MOVEMENT"),
        @JsonSubTypes.Type(value = RuleSurfaceMovement.class, name = "SWIMMING_MOVEMENT"),
        @JsonSubTypes.Type(value = RuleFlightFalling.class, name = "FLIGHT_FALLING"),
        @JsonSubTypes.Type(value = RuleHoverPrevention.class, name = "HOVER_PREVENTION"),
        @JsonSubTypes.Type(value = RuleHighJump.class, name = "HIGH_JUMP"),
        @JsonSubTypes.Type(value = RuleLongJump.class, name = "LONG_JUMP"),
        @JsonSubTypes.Type(value = RuleSpecialSpeedSelection.class, name = "SPECIAL_SPEED_SELECTION"),
        @JsonSubTypes.Type(value = RuleSpecialSpeedAdjustment.class, name = "SPECIAL_SPEED_ADJUSTMENT"),
        @JsonSubTypes.Type(value = RuleSwimSpeedException.class, name = "SWIM_SPEED_EXCEPTION"),
        @JsonSubTypes.Type(value = RuleGridMovementCost.class, name = "GRID_MOVEMENT_COST"),
        @JsonSubTypes.Type(value = RuleDiagonalMovementCost.class, name = "DIAGONAL_MOVEMENT_COST"),
        @JsonSubTypes.Type(value = RuleCrawlingMovement.class, name = "CRAWLING_MOVEMENT"),
        @JsonSubTypes.Type(value = RuleClimbSpeedException.class, name = "CLIMB_SPEED_EXCEPTION"),
        @JsonSubTypes.Type(value = RuleFlySpeedCapability.class, name = "FLY_SPEED_CAPABILITY"),
        @JsonSubTypes.Type(value = RuleJumpProcedureSelection.class, name = "JUMP_PROCEDURE_SELECTION")
})
public interface RuleMovementDetails {
    String kind();
}
