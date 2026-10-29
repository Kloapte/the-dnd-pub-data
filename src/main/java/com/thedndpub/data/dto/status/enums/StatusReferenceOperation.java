package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusReferenceOperation {
    @JsonProperty("END_STATUS") END_STATUS,
    @JsonProperty("END_CONDITION") END_CONDITION
}
