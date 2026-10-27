package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckResolution {
    @JsonProperty("against-dc") AGAINST_DC,
    @JsonProperty("contested-check") CONTESTED_CHECK
}
