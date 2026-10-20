package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record ExpectedEvidence(MechanicsEvidence evidence, String claimPath, EvidenceRole role) {
    public ExpectedEvidence {
        ModelChecks.required(evidence, "evidence");
        ModelChecks.pointer(claimPath);
        ModelChecks.require(claimPath.equals("/payload") || claimPath.startsWith("/payload/")
                        || claimPath.equals("/context") || claimPath.startsWith("/context/"),
                "Expected evidence must address a payload or context claim");
        ModelChecks.required(role, "role");
        MechanicEvidenceAssociation.of(evidence, claimPath, role);
    }
}
