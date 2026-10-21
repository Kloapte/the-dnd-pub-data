package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsDocumentRevision(
        @JsonProperty(required = true) long number,
        String contentSha256,
        Integer hashProtocolVersion) {
    public MechanicsDocumentRevision(long number, String contentSha256) {
        this(number, contentSha256, null);
    }

    public MechanicsDocumentRevision {
        ModelChecks.require(number > 0, "Document revision must be positive");
        ModelChecks.digest(contentSha256);
        ModelChecks.require(hashProtocolVersion == null
                        || hashProtocolVersion == MechanicsHashes.LEGACY_DOCUMENT_HASH_PROTOCOL
                        || hashProtocolVersion == MechanicsHashes.DOCUMENT_HASH_PROTOCOL,
                "Unsupported mechanics document hash protocol");
    }

    public int effectiveHashProtocolVersion() {
        return hashProtocolVersion == null ? MechanicsHashes.LEGACY_DOCUMENT_HASH_PROTOCOL : hashProtocolVersion;
    }
}
