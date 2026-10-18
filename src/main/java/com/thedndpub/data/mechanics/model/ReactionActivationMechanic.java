package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReactionActivationMechanic(
        ReactionActionCost cost,
        TriggerExpression trigger) implements MechanicPayload {
    public ReactionActivationMechanic {
        ModelChecks.required(cost, "cost");
        ModelChecks.required(trigger, "trigger");
    }
}
