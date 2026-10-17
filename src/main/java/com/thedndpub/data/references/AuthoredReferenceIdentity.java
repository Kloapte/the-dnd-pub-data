package com.thedndpub.data.references;

import java.util.Objects;

public record AuthoredReferenceIdentity(String originalToken, String originalSource) {
    public AuthoredReferenceIdentity {
        Objects.requireNonNull(originalToken, "originalToken");
    }
}
