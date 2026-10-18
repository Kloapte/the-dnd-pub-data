package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EffectDurationMechanic(
        DurationSpecification specification) implements MechanicPayload {
    public EffectDurationMechanic {
        ModelChecks.required(specification, "specification");
    }
}
