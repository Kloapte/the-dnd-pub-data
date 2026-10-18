package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ObservationRevisionReference(String id, String contentSha256) {
    public ObservationRevisionReference {
        ModelChecks.text(id, "observation ID");
        ModelChecks.digest(contentSha256);
    }

    public static ObservationRevisionReference of(MechanicsObservationReference observation) {
        return new ObservationRevisionReference(observation.id(), MechanicsHashes.observationHash(observation));
    }
}
