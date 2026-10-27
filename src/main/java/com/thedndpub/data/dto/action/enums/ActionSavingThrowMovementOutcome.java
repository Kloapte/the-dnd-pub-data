package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionSavingThrowMovementOutcome {
    @JsonProperty("push-five-feet") PUSH_FIVE_FEET,
    @JsonProperty("prone") PRONE
}
