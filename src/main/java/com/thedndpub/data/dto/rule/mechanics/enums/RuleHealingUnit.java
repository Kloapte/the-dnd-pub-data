package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleHealingUnit {
    @JsonProperty("hit-points") HIT_POINTS,
    @JsonProperty("hit-dice") HIT_DICE
}
