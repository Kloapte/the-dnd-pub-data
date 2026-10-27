package com.thedndpub.data.dto.condition.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ConditionSense {
    @JsonProperty("sight") SIGHT,
    @JsonProperty("hearing") HEARING,
    @JsonProperty("smell") SMELL,
    @JsonProperty("taste") TASTE,
    @JsonProperty("touch") TOUCH,
    @JsonProperty("special") SPECIAL
}
