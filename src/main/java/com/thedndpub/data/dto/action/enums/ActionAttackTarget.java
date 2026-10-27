package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionAttackTarget {
    @JsonProperty("armor-class") ARMOR_CLASS,
    @JsonProperty("creature") CREATURE,
    @JsonProperty("object") OBJECT
}
