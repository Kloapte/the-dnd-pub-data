package com.thedndpub.data.references;

import java.util.Objects;

public record ReferenceOwner(String catalog, String stableId) {
    public ReferenceOwner {
        Objects.requireNonNull(catalog, "catalog");
        Objects.requireNonNull(stableId, "stableId");
    }
}
