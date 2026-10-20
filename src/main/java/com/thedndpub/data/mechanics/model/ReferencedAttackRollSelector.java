package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReferencedAttackRollSelector(MechanicRevisionReference attackRoll) implements AttackRollSelector {
    public ReferencedAttackRollSelector {
        ModelChecks.required(attackRoll, "attackRoll");
    }
}
