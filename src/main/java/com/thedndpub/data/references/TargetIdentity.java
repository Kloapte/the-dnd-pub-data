package com.thedndpub.data.references;

import java.util.Objects;

public record TargetIdentity(
        ReferenceTargetKind targetKind,
        String stableId,
        String name,
        String source) {
    public TargetIdentity {
        Objects.requireNonNull(targetKind, "targetKind");
        Objects.requireNonNull(stableId, "stableId");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
    }
}
