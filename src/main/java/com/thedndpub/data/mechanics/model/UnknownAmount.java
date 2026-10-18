package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UnknownAmount(
        UnknownValueReason reason) implements AmountExpression {
    public UnknownAmount {
        ModelChecks.required(reason, "reason");
    }
}
