package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusMechanicKind {
    @JsonProperty("HIT_POINT_THRESHOLD") HIT_POINT_THRESHOLD,
    @JsonProperty("TURN_RESTRICTION") TURN_RESTRICTION,
    @JsonProperty("ROLL_MODIFIER") ROLL_MODIFIER,
    @JsonProperty("MAINTENANCE_SAVE") MAINTENANCE_SAVE,
    @JsonProperty("ENDING_CONDITION") ENDING_CONDITION
}
