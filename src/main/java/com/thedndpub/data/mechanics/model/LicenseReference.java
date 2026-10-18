package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LicenseReference(
        String id,
        String version,
        String url) {
    public LicenseReference {
        ModelChecks.text(id, "license ID");
        ModelChecks.text(version, "license version");
        ModelChecks.https(url);
    }
}
