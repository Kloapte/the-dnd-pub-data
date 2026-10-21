package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Associates a saving throw or damage result with its source-defined area effect. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AppliesToAreaEffect(
        MechanicRevisionReference areaEffect,
        List<String> evidenceIds) implements MechanicContextBinding {
    public AppliesToAreaEffect {
        ModelChecks.required(areaEffect, "areaEffect");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Area-effect binding requires evidence");
    }
}
