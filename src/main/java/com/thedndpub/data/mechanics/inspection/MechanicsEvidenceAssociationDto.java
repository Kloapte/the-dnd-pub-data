package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsEvidenceAssociationDto(
        String evidenceId,
        String claimPath,
        EvidenceRole role) {
    public MechanicsEvidenceAssociationDto {
        ModelChecks.text(evidenceId, "evidenceId");
        ModelChecks.pointer(claimPath);
        ModelChecks.required(role, "role");
    }
}
