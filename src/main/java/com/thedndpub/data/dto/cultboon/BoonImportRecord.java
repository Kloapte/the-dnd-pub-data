package com.thedndpub.data.dto.cultboon;

import java.util.Objects;

public record BoonImportRecord(BoonDefinition definition, BoonImportProvenance provenance) {
    public BoonImportRecord {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(provenance, "provenance");
    }
}
