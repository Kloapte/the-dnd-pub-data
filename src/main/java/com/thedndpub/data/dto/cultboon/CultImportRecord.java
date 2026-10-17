package com.thedndpub.data.dto.cultboon;

import java.util.Objects;

public record CultImportRecord(CultDefinition definition, CultImportProvenance provenance) {
    public CultImportRecord {
        Objects.requireNonNull(definition, "definition");
        Objects.requireNonNull(provenance, "provenance");
    }
}
