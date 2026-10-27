package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionLevelApplication {
    @JsonProperty("add-specified-levels") ADD_SPECIFIED_LEVELS,
    @JsonProperty("add-one-level") ADD_ONE_LEVEL
}
