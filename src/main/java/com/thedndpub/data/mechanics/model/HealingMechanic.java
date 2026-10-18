package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HealingMechanic(
        AmountExpression amount,
        HealingMode mode) implements MechanicPayload {
    public HealingMechanic {
        ModelChecks.required(amount, "amount");
        ModelChecks.required(mode, "mode");
    }
}
