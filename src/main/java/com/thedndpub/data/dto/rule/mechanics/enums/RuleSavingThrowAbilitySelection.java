package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleSavingThrowAbilitySelection {
    @JsonProperty("target-chooses") TARGET_CHOOSES,
    @JsonProperty("rule-specified") RULE_SPECIFIED,
    @JsonProperty("effect") EFFECT
}
