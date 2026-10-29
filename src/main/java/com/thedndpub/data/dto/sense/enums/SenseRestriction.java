package com.thedndpub.data.dto.sense.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum SenseRestriction {
    @JsonProperty("cannot-detect-flying") CANNOT_DETECT_FLYING,
    @JsonProperty("cannot-detect-incorporeal") CANNOT_DETECT_INCORPOREAL,
    @JsonProperty("requires-contact") REQUIRES_CONTACT,
    @JsonProperty("requires-surface") REQUIRES_SURFACE
}
