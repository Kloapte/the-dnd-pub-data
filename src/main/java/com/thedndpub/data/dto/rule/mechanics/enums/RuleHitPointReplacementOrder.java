package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleHitPointReplacementOrder {
    @JsonProperty("choose-source") CHOOSE_SOURCE,
    @JsonProperty("newest-first") NEWEST_FIRST
}
