package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/** An authored action/check event that ends an existing condition; it is descriptive, not executable. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConditionTerminationEventMechanic(
        ConditionTerminationActor actor,
        Ability checkAbility) implements MechanicPayload {
    public ConditionTerminationEventMechanic {
        ModelChecks.required(actor, "actor");
    }
}
