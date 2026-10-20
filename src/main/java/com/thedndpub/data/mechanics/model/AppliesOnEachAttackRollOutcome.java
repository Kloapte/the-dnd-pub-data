package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Binds an effect to the outcome of the corresponding independent attack instance. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AppliesOnEachAttackRollOutcome(
        MechanicRevisionReference attackRoll,
        AttackRollOutcome outcome,
        AttackRollInstanceScope instanceScope,
        List<String> evidenceIds) implements MechanicContextBinding {
    public AppliesOnEachAttackRollOutcome {
        ModelChecks.required(attackRoll, "attackRoll");
        ModelChecks.required(outcome, "outcome");
        ModelChecks.required(instanceScope, "instanceScope");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Per-instance attack-roll outcome requires evidence");
    }
}
