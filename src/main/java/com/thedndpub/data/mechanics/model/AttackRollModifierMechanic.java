package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** A source-backed roll-quality modifier, separate from the attack event and its applicability conditions. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttackRollModifierMechanic(
        AttackRollModifier modifier,
        AttackRollSelector affectedRolls) implements MechanicPayload {
    public AttackRollModifierMechanic {
        ModelChecks.required(modifier, "modifier");
        ModelChecks.required(affectedRolls, "affectedRolls");
    }
}
