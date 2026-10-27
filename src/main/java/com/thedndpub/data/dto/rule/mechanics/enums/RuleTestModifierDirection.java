package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleTestModifierDirection {
    @JsonProperty("bonus") BONUS,
    @JsonProperty("penalty") PENALTY
}
