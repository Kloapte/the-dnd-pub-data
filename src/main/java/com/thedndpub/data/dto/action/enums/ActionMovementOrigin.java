package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionMovementOrigin {
    @JsonProperty("action") ACTION,
    @JsonProperty("bonus-action") BONUS_ACTION,
    @JsonProperty("reaction") REACTION,
    @JsonProperty("speed") SPEED
}
