package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleObscurationLevel {
    @JsonProperty("lightly-obscured") LIGHTLY_OBSCURED,
    @JsonProperty("heavily-obscured") HEAVILY_OBSCURED
}
