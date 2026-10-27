package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionSavingThrowFailureOutcome {
    @JsonProperty("condition-applied") CONDITION_APPLIED,
    @JsonProperty("target-moved") TARGET_MOVED,
    @JsonProperty("effect-applied") EFFECT_APPLIED
}
