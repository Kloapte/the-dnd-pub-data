package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record EvidenceRevisionReference(String id, String contentSha256) {
    public EvidenceRevisionReference {
        ModelChecks.text(id, "evidence ID");
        ModelChecks.digest(contentSha256);
    }

    public static EvidenceRevisionReference of(MechanicsEvidence evidence) {
        return new EvidenceRevisionReference(evidence.id(), MechanicsHashes.evidenceHash(evidence));
    }
}
