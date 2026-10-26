package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CanonicalRecordIdentity(String id, String name, String domain) {
    public CanonicalRecordIdentity {
        if (id == null || name == null || name.isBlank() || domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Canonical record identity requires id, name, and domain");
        }
        if (!id.startsWith(domain + ":")) {
            throw new IllegalArgumentException("Canonical ID type does not match domain: " + id);
        }
    }
}
