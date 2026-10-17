package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.treasure.TreasureHoardDte;

import java.util.Objects;

public record TreasureHoardDefinition(
        TreasureTableId id,
        String name,
        SourceReference source,
        TreasureHoardDte authoredData) implements TreasureTableDefinition {
    public TreasureHoardDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredData, "authoredData");
    }

    @Override
    public TreasureTableKind kind() {
        return TreasureTableKind.HOARD;
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
        return authoredData.crMin() + "-" + authoredData.crMax();
    }
}
