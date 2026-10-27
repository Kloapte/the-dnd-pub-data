package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleMovementProcedure {
    @JsonProperty("movement-cost") MOVEMENT_COST,
    @JsonProperty("distance-limit") DISTANCE_LIMIT,
    @JsonProperty("fall-damage") FALL_DAMAGE,
    @JsonProperty("speed-required") SPEED_REQUIRED,
    @JsonProperty("destination") DESTINATION,
    @JsonProperty("movement-mode") MOVEMENT_MODE,
    @JsonProperty("speed-selection") SPEED_SELECTION,
    @JsonProperty("speed-adjustment") SPEED_ADJUSTMENT,
    @JsonProperty("fall-trigger") FALL_TRIGGER,
    @JsonProperty("fall-prevention") FALL_PREVENTION,
    @JsonProperty("jump-distance") JUMP_DISTANCE,
    @JsonProperty("grid-movement-cost") GRID_MOVEMENT_COST
}
