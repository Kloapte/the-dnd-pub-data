package com.thedndpub.data.dto.status.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StatusRoll {
    @JsonProperty("initiative") INITIATIVE,
    @JsonProperty("attack") ATTACK
}
