package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** Establishes a persistent spell effect only after the referenced initial attack hits. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HitGatedEffectEstablishment(MechanicRevisionReference attackRoll, AttackRollOutcome outcome) {
    public HitGatedEffectEstablishment {
        ModelChecks.required(attackRoll, "attackRoll");
        ModelChecks.require(outcome == AttackRollOutcome.HIT,
                "The Witch Bolt persistent link is established only on HIT");
    }

    public HitGatedEffectEstablishment(MechanicRevisionReference attackRoll) {
        this(attackRoll, AttackRollOutcome.HIT);
    }
}
