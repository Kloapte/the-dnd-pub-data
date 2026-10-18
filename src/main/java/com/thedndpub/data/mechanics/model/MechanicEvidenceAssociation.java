package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicEvidenceAssociation(
        String evidenceId,
        String claimPath,
        EvidenceRole role,
        String evidenceSha256) {
    public MechanicEvidenceAssociation {
        ModelChecks.text(evidenceId, "evidenceId");
        ModelChecks.required(role, "role");
        ModelChecks.digest(evidenceSha256);
        ModelChecks.pointer(claimPath);
        ModelChecks.require(claimPath.equals("/payload") || claimPath.startsWith("/payload/")
                || claimPath.equals("/context") || claimPath.startsWith("/context/"),
                "Evidence must address a payload or context assertion");
        if (role == EvidenceRole.EFFECT_ROLE) {
            ModelChecks.require(claimPath.equals("/payload"), "Effect-role evidence must support the interpreted payload");
        }
    }

    public static MechanicEvidenceAssociation of(MechanicsEvidence evidence, String claimPath, EvidenceRole role) {
        return new MechanicEvidenceAssociation(evidence.id(), claimPath, role, MechanicsHashes.evidenceHash(evidence));
    }
}
