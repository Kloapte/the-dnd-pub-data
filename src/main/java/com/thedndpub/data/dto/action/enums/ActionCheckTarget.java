package com.thedndpub.data.dto.action.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ActionCheckTarget {
    @JsonProperty("target") TARGET,
    @JsonProperty("affected-creature") AFFECTED_CREATURE
}
