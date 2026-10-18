package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DamageMechanic(
        AmountExpression amount,
        DamageTypeSelection damageType) implements MechanicPayload {
    public DamageMechanic {
        ModelChecks.required(amount, "amount");
        ModelChecks.required(damageType, "damageType");
    }
}
