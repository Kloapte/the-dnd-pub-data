package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleRestRecovery {
    @JsonProperty("hit-points") HIT_POINTS,
    @JsonProperty("hit-dice") HIT_DICE,
    @JsonProperty("class-feature") CLASS_FEATURE,
    @JsonProperty("spell-slots") SPELL_SLOTS
}
