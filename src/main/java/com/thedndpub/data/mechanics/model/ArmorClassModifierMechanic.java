package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ArmorClassModifierMechanic(
        ModifierAmount amount) implements MechanicPayload {
    public ArmorClassModifierMechanic {
        ModelChecks.required(amount, "amount");
    }
}
