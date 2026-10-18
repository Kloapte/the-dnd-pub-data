package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AppliesForDuration(
        DurationSpecification duration,
        List<String> evidenceIds) implements MechanicContextBinding {
    public AppliesForDuration {
        ModelChecks.required(duration, "duration");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Duration binding requires evidence");
    }
}
