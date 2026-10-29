package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusTurnRestrictionKind {
    @JsonProperty("cannot-move") CANNOT_MOVE,
    @JsonProperty("cannot-take-action") CANNOT_TAKE_ACTION,
    @JsonProperty("cannot-take-reaction") CANNOT_TAKE_REACTION,
    @JsonProperty("movement-limited") MOVEMENT_LIMITED
}
