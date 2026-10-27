package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleCoverLevel {
    @JsonProperty("half") HALF,
    @JsonProperty("three-quarters") THREE_QUARTERS,
    @JsonProperty("total") TOTAL
}
