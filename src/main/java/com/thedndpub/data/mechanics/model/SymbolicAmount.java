package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SymbolicAmount(
        AmountSymbol symbol) implements AmountExpression {
    public SymbolicAmount {
        ModelChecks.required(symbol, "symbol");
    }
}
