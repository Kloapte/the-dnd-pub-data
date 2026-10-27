package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleDeathProcedure {
    @JsonProperty("instant-death") INSTANT_DEATH,
    @JsonProperty("massive-damage") MASSIVE_DAMAGE,
    @JsonProperty("dying") DYING,
    @JsonProperty("stabilized") STABILIZED
}
