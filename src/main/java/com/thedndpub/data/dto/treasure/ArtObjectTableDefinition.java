package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.treasure.ArtObjectTableDte;

import java.util.Objects;

public record ArtObjectTableDefinition(
        TreasureTableId id,
        String name,
        SourceReference source,
        ArtObjectTableDte authoredData) implements TreasureTableDefinition {
    public ArtObjectTableDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredData, "authoredData");
    }

    @Override
    public TreasureTableKind kind() {
        return TreasureTableKind.ART_OBJECTS;
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
