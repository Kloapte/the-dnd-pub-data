package com.thedndpub.data.dto.rule.mechanics.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RuleMagicResource {
    @JsonProperty("spell-slot") SPELL_SLOT,
    @JsonProperty("spell-point") SPELL_POINT,
    @JsonProperty("charge") CHARGE
}
