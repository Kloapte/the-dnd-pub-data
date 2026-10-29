package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusSaveAbility {
    @JsonProperty("strength") STRENGTH,
    @JsonProperty("dexterity") DEXTERITY,
    @JsonProperty("constitution") CONSTITUTION,
    @JsonProperty("intelligence") INTELLIGENCE,
    @JsonProperty("wisdom") WISDOM,
    @JsonProperty("charisma") CHARISMA
}
