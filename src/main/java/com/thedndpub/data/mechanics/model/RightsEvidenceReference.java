package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RightsEvidenceReference(
        String id,
        String evidenceSha256,
        String locator) {
    public RightsEvidenceReference {
        ModelChecks.text(id, "rights evidence ID");
        ModelChecks.digest(evidenceSha256);
        ModelChecks.text(locator, "rights evidence locator");
    }
}
