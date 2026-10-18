package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RightsDecisionReference(
        String id,
        String authorityReference,
        String recordSha256) {
    public RightsDecisionReference {
        ModelChecks.text(id, "rights decision ID");
        ModelChecks.text(authorityReference, "rights authority");
        ModelChecks.digest(recordSha256);
    }
}
