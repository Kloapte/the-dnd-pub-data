package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusThresholdComparison {
    @JsonProperty("at-or-below") AT_OR_BELOW,
    @JsonProperty("below") BELOW
}
