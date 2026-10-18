package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record UntilTurnBoundary(
        TurnActor actor,
        TurnBoundary boundary) implements DurationSpecification {
    public UntilTurnBoundary {
        ModelChecks.required(actor, "actor");
        ModelChecks.required(boundary, "boundary");
    }
}
