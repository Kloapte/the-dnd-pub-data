package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.treasure.MagicItemTableDte;

import java.util.Objects;

public record MagicItemTableDefinition(
        TreasureTableId id,
        String name,
        SourceReference source,
        MagicItemTableDte authoredData) implements TreasureTableDefinition {
    public MagicItemTableDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredData, "authoredData");
    }

    @Override
    public TreasureTableKind kind() {
        return TreasureTableKind.MAGIC_ITEMS;
    }

    @Override
    public int tableCount() {
        return 1 + (int) authoredData.table().stream().filter(row -> row.table() != null).count();
    }

    @Override
    public int rowCount() {
        return authoredData.table().stream().mapToInt(row -> 1 + (row.table() == null ? 0 : row.table().size())).sum();
    }

    @Override
    public String category() {
        return authoredData.type();
    }
}
