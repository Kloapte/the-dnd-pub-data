package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusSaveFailure {
    @JsonProperty("status-ends") STATUS_ENDS,
    @JsonProperty("effect-ends") EFFECT_ENDS
}
