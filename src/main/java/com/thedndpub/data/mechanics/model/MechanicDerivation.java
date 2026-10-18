package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicDerivation(
        DerivationMethod method,
        String patternId,
        String patternVersion,
        String extractorVersion,
        List<ObservationRevisionReference> inputObservations,
        List<SourceArtifactRevision> sources) {
    public MechanicDerivation {
        ModelChecks.required(method, "method");
        ModelChecks.text(patternId, "patternId");
        ModelChecks.text(patternVersion, "patternVersion");
        ModelChecks.text(extractorVersion, "extractorVersion");
        inputObservations = ModelChecks.list(inputObservations);
        sources = ModelChecks.list(sources);
        ModelChecks.require(!sources.isEmpty(), "Derivation must bind its source resource revisions");
    }
}
