package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionAbility {
    @JsonProperty("strength") STRENGTH,
    @JsonProperty("dexterity") DEXTERITY,
    @JsonProperty("constitution") CONSTITUTION,
    @JsonProperty("intelligence") INTELLIGENCE,
    @JsonProperty("wisdom") WISDOM,
    @JsonProperty("charisma") CHARISMA
}
