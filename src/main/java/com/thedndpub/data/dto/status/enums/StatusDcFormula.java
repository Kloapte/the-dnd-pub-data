package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusDcFormula {
    @JsonProperty("fixed") FIXED,
    @JsonProperty("higher-of-base-or-damage-fraction") HIGHER_OF_BASE_OR_DAMAGE_FRACTION
}
