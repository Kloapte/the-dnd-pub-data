package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActivatedBy(
        MechanicRevisionReference activation,
        List<String> evidenceIds) implements MechanicContextBinding {
    public ActivatedBy {
        ModelChecks.required(activation, "activation");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Activation binding requires evidence");
    }
}
