package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionRecoveryRequirement {
    @JsonProperty("food") FOOD,
    @JsonProperty("drink") DRINK
}
