package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionSavingThrowSuccessOutcome {
    @JsonProperty("no-effect") NO_EFFECT,
    @JsonProperty("check-succeeds") CHECK_SUCCEEDS
}
