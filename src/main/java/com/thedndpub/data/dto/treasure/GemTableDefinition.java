package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.treasure.GemTableDte;

import java.util.Objects;

public record GemTableDefinition(
        TreasureTableId id,
        String name,
        SourceReference source,
        GemTableDte authoredData) implements TreasureTableDefinition {
    public GemTableDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredData, "authoredData");
    }

    @Override
    public TreasureTableKind kind() {
        return TreasureTableKind.GEMS;
    }

    @Override
    public int tableCount() {
        return 1;
    }

    @Override
    public int rowCount() {
        return authoredData.table().size();
    }

    @Override
    public String category() {
        return Integer.toString(authoredData.type());
    }
}
