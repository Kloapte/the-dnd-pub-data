package com.thedndpub.data.dto.skill.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SkillAbility {
    @JsonProperty("strength") STRENGTH,
    @JsonProperty("dexterity") DEXTERITY,
    @JsonProperty("constitution") CONSTITUTION,
    @JsonProperty("intelligence") INTELLIGENCE,
    @JsonProperty("wisdom") WISDOM,
    @JsonProperty("charisma") CHARISMA
}
