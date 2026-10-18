package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Version-one claim fingerprint payload. Review and rights decisions do not change the substantive claim. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicRevisionContent(
        MechanicId id,
        MechanicPayload payload,
        MechanicContext context,
        List<MechanicEvidenceAssociation> evidenceAssociations,
        MechanicDerivation derivation,
        MechanicsAssessment assessment) {
    public MechanicRevisionContent {
        ModelChecks.required(id, "id");
        ModelChecks.required(payload, "payload");
        ModelChecks.required(context, "context");
        ModelChecks.required(derivation, "derivation");
        ModelChecks.required(assessment, "assessment");
        evidenceAssociations = ModelChecks.list(evidenceAssociations);
    }
}
