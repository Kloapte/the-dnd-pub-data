package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusSaveTrigger {
    @JsonProperty("damage") DAMAGE,
    @JsonProperty("environmental-phenomenon") ENVIRONMENTAL_PHENOMENON
}
