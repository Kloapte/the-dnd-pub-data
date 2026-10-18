package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsProcessing(
        ProcessingState state,
        String adapterId,
        String adapterVersion) {
    public MechanicsProcessing {
        ModelChecks.required(state, "state");
        if (state != ProcessingState.NOT_ATTEMPTED) {
            ModelChecks.text(adapterId, "adapterId");
            ModelChecks.text(adapterVersion, "adapterVersion");
        }
        ModelChecks.require((adapterId == null) == (adapterVersion == null), "Adapter identity and version form a pair");
    }
}
