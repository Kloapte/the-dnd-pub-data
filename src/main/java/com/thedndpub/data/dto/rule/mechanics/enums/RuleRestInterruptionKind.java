package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleRestInterruptionKind {
    @JsonProperty("combat") COMBAT,
    @JsonProperty("strenuous-activity") STRENUOUS_ACTIVITY,
    @JsonProperty("none") NONE
}
