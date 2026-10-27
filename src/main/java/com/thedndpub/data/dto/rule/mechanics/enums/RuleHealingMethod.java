package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleHealingMethod {
    @JsonProperty("hit-dice") HIT_DICE,
    @JsonProperty("rest") REST,
    @JsonProperty("healing-effect") HEALING_EFFECT,
    @JsonProperty("stabilization") STABILIZATION
}
