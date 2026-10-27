package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckOpposingAbilitySelection {
    @JsonProperty("target-chooses") TARGET_CHOOSES,
    @JsonProperty("acting-creature-chooses") ACTING_CREATURE_CHOOSES
}
