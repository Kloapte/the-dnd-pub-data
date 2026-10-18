package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DiceAmount(
        @JsonProperty(required = true) int count,
        @JsonProperty(required = true) int sides,
        @JsonProperty(required = true) int modifier) implements AmountExpression {
    public DiceAmount {
        ModelChecks.require(count > 0 && sides > 0, "Dice count and sides must be positive");
    }
}
