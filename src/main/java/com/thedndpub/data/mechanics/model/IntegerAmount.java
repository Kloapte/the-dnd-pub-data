package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record IntegerAmount(
        @JsonProperty(required = true) int value) implements AmountExpression {
    public IntegerAmount {
        ModelChecks.require(value >= 0, "Amount cannot be negative");
    }
}
