package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.DerivationMethod;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsDerivationDto(
        DerivationMethod method,
        String patternId,
        String patternVersion,
        String extractorVersion,
        List<String> inputObservationIds) {
    public MechanicsDerivationDto {
        ModelChecks.required(method, "method");
        ModelChecks.text(patternId, "patternId");
        ModelChecks.text(patternVersion, "patternVersion");
        ModelChecks.text(extractorVersion, "extractorVersion");
        inputObservationIds = ModelChecks.list(inputObservationIds);
    }
}
