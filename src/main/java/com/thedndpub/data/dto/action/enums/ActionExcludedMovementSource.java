package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionExcludedMovementSource {
    @JsonProperty("disengage") DISENGAGE,
    @JsonProperty("teleportation") TELEPORTATION,
    @JsonProperty("involuntary-movement") INVOLUNTARY_MOVEMENT
}
