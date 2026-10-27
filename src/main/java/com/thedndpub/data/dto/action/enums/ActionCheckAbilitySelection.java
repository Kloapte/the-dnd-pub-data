package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckAbilitySelection {
    @JsonProperty("acting-creature-chooses") ACTING_CREATURE_CHOOSES,
    @JsonProperty("target-chooses") TARGET_CHOOSES,
    @JsonProperty("DM-DETERMINES") DM_DETERMINES
}
