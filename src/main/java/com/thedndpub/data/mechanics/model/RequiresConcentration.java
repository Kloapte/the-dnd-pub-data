package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** An explicit concentration requirement attached to a condition effect. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RequiresConcentration(List<String> evidenceIds) implements MechanicContextBinding {
    public RequiresConcentration {
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Concentration relationship requires evidence");
    }
}
