package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleAreaShape {
    @JsonProperty("cone") CONE,
    @JsonProperty("cube") CUBE,
    @JsonProperty("cylinder") CYLINDER,
    @JsonProperty("emanation") EMANATION,
    @JsonProperty("line") LINE,
    @JsonProperty("sphere") SPHERE
}
