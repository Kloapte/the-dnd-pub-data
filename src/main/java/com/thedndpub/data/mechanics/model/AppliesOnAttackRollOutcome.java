package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Binds a source-supported effect result to one exact attack-roll claim revision. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AppliesOnAttackRollOutcome(
        MechanicRevisionReference attackRoll,
        AttackRollOutcome outcome,
        List<String> evidenceIds) implements MechanicContextBinding {
    public AppliesOnAttackRollOutcome {
        ModelChecks.required(attackRoll, "attackRoll");
        ModelChecks.required(outcome, "outcome");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Attack-roll outcome binding requires evidence");
    }
}
