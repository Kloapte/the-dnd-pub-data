package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ReferenceResolution(
        CanonicalReference reference,
        State state,
        CanonicalRecordIdentity canonicalTarget) {
    public enum State {
        CANONICAL_TARGET_AVAILABLE,
        SOURCE_KNOWN_WITHOUT_CANONICAL_OUTPUT,
        SOURCE_IDENTITY_UNKNOWN
    }

    public ReferenceResolution {
        if (reference == null || state == null) {
            throw new IllegalArgumentException("Reference resolution requires a reference and state");
        }
        if ((state == State.CANONICAL_TARGET_AVAILABLE) != (canonicalTarget != null)) {
            throw new IllegalArgumentException("Only canonical targets may carry a canonical record");
        }
    }
}
