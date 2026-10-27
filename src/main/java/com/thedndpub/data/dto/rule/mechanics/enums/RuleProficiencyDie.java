package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleProficiencyDie {
    @JsonProperty("d4") D4,
    @JsonProperty("d6") D6,
    @JsonProperty("d8") D8,
    @JsonProperty("d10") D10,
    @JsonProperty("d12") D12
}
