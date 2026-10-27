package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleAreaOrigin {
    @JsonProperty("source") SOURCE,
    @JsonProperty("point-in-space") POINT_IN_SPACE,
    @JsonProperty("creature") CREATURE,
    @JsonProperty("object") OBJECT,
    @JsonProperty("shape-defined") SHAPE_DEFINED
}
