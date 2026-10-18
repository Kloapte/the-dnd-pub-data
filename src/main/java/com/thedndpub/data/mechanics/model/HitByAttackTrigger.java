package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record HitByAttackTrigger(
        AttackEventKind event) implements TriggerExpression {
    public HitByAttackTrigger {
        ModelChecks.required(event, "event");
    }
}
