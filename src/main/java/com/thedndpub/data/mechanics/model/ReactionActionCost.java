package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReactionActionCost(
        @JsonProperty(required = true) int amount) {
    public ReactionActionCost {
        ModelChecks.require(amount > 0, "Reaction action cost must be positive");
    }
}
