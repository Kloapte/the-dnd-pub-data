package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionRecoveryMethod {
    @JsonProperty("long-rest") LONG_REST,
    @JsonProperty("raised-from-dead") RAISED_FROM_DEAD
}
