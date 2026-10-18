package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AttributionReference(
        String id,
        String version,
        String noticeSha256) {
    public AttributionReference {
        ModelChecks.text(id, "attribution ID");
        ModelChecks.text(version, "attribution version");
        ModelChecks.digest(noticeSha256);
    }
}
