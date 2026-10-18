package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record LicensedDocumentReference(
        String id,
        String version,
        String url,
        String documentSha256) {
    public LicensedDocumentReference {
        ModelChecks.text(id, "document ID");
        ModelChecks.text(version, "document version");
        ModelChecks.https(url);
        ModelChecks.digest(documentSha256);
    }
}
