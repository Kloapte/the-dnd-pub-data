package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckFailure {
    @JsonProperty("no-effect") NO_EFFECT,
    @JsonProperty("target-remains-unstable") TARGET_REMAINS_UNSTABLE
}
