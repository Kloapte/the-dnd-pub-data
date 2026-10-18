package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicInspectionDto(
        MechanicId id,
        @JsonProperty(required = true) long revision,
        MechanicPayload payload,
        MechanicContext context,
        List<MechanicsEvidenceAssociationDto> evidenceAssociations,
        MechanicsDerivationDto derivation,
        MechanicsAssessment assessment,
        MechanicsReviewDto review,
        MechanicsLicensingDto licensing) {
    public MechanicInspectionDto {
        ModelChecks.required(id, "id");
        ModelChecks.require(revision > 0, "Mechanic revision must be positive");
        ModelChecks.required(payload, "payload");
        ModelChecks.required(context, "context");
        ModelChecks.required(derivation, "derivation");
        ModelChecks.required(assessment, "assessment");
        ModelChecks.required(review, "review");
        ModelChecks.required(licensing, "licensing");
        evidenceAssociations = ModelChecks.list(evidenceAssociations);
    }
}
