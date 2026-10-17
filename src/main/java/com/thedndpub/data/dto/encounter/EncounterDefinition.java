package com.thedndpub.data.dto.encounter;

import com.thedndpub.data.dto.source.SourceReference;
import java.util.List;
import java.util.Objects;

public record EncounterDefinition(EncounterId id, String name, SourceReference source, List<EncounterTable> tables) {
    public EncounterDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        tables = List.copyOf(tables);
    }

    public int rowCount() {
        return tables.stream().mapToInt(table -> table.rows().size()).sum();
    }
}
