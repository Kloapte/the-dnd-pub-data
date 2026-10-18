package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsTransformationReference(
        String transformationId,
        String dialectVersion,
        String instructionEvidenceId) {
    public MechanicsTransformationReference {
        ModelChecks.text(transformationId, "transformationId");
        ModelChecks.text(dialectVersion, "dialectVersion");
        ModelChecks.text(instructionEvidenceId, "instructionEvidenceId");
    }
}
