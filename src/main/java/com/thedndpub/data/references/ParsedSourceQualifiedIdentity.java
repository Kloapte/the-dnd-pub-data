package com.thedndpub.data.references;

import java.util.Objects;

public record ParsedSourceQualifiedIdentity(String name, String source, String configuration) {
    public ParsedSourceQualifiedIdentity {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
    }
}
