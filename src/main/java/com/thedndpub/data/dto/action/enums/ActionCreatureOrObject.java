package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCreatureOrObject {
    @JsonProperty("creature") CREATURE,
    @JsonProperty("object") OBJECT
}
