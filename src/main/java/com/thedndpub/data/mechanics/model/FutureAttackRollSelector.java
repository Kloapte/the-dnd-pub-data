package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record FutureAttackRollSelector(
        AttackRollParticipant actor,
        AttackRollParticipant target) implements AttackRollSelector {
    public FutureAttackRollSelector {
        ModelChecks.required(actor, "actor");
        ModelChecks.required(target, "target");
    }
}
