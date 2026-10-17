package com.thedndpub.data.dto.nametable;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;
import java.util.Objects;

public record NameTableDefinition(
        NameTableId id,
        String name,
        SourceReference source,
        List<NameTableOption> tables) {
    public NameTableDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        tables = List.copyOf(tables);
    }

    public int rowCount() {
        return tables.stream().mapToInt(table -> table.rows().size()).sum();
    }
}
