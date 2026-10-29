package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusEndingWhen {
    @JsonProperty("another-concentration-spell-starts") ANOTHER_CONCENTRATION_SPELL_STARTS,
    @JsonProperty("another-concentration-effect-starts") ANOTHER_CONCENTRATION_EFFECT_STARTS,
    @JsonProperty("incapacitated") INCAPACITATED,
    @JsonProperty("dies") DIES,
    @JsonProperty("effect-duration-expires") EFFECT_DURATION_EXPIRES,
    @JsonProperty("voluntarily") VOLUNTARILY
}
