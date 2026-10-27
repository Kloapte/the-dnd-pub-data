package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionRestriction {
    @JsonProperty("cannot-move") CANNOT_MOVE,
    @JsonProperty("cannot-take-actions") CANNOT_TAKE_ACTIONS,
    @JsonProperty("cannot-take-reactions") CANNOT_TAKE_REACTIONS,
    @JsonProperty("cannot-speak") CANNOT_SPEAK,
    @JsonProperty("cannot-see") CANNOT_SEE,
    @JsonProperty("cannot-target") CANNOT_TARGET,
    @JsonProperty("movement-limited") MOVEMENT_LIMITED
}
