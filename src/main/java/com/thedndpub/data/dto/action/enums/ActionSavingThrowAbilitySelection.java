package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionSavingThrowAbilitySelection {
    @JsonProperty("target-chooses") TARGET_CHOOSES,
    @JsonProperty("rule-specified") RULE_SPECIFIED
}
