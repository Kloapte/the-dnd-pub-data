package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckActor {
    @JsonProperty("acting-creature") ACTING_CREATURE,
    @JsonProperty("target") TARGET,
    @JsonProperty("both") BOTH
}
