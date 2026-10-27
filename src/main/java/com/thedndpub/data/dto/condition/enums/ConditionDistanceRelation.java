package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionDistanceRelation {
    @JsonProperty("within") WITHIN,
    @JsonProperty("beyond") BEYOND
}
