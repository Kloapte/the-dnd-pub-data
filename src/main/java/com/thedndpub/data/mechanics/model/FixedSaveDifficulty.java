package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FixedSaveDifficulty(
        @JsonProperty(required = true) int value) implements SaveDifficulty {
    public FixedSaveDifficulty {
        ModelChecks.require(value >= 0, "Save difficulty cannot be negative");
    }
}
