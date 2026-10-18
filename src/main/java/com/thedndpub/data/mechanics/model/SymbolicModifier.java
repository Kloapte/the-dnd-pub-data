package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SymbolicModifier(
        AmountSymbol symbol) implements ModifierAmount {
    public SymbolicModifier {
        ModelChecks.required(symbol, "symbol");
    }
}
