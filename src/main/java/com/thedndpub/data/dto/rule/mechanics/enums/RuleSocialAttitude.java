package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleSocialAttitude {
    @JsonProperty("friendly") FRIENDLY,
    @JsonProperty("indifferent") INDIFFERENT,
    @JsonProperty("hostile") HOSTILE
}
