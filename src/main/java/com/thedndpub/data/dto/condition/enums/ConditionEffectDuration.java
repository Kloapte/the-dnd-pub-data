package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionEffectDuration {
    @JsonProperty("while-level-threshold-is-met") WHILE_LEVEL_THRESHOLD_IS_MET,
    @JsonProperty("while-condition-active") WHILE_CONDITION_ACTIVE
}
