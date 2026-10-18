package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReferencedDuration(
        MechanicRevisionReference mechanic) implements DurationSpecification {
    public ReferencedDuration {
        ModelChecks.required(mechanic, "mechanic");
    }
}
