package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.treasure.DragonTreasureDte;

import java.util.Objects;

public record DragonTreasureDefinition(
        TreasureTableId id,
        String name,
        SourceReference source,
        DragonTreasureDte authoredData,
        int sharedMundaneItemRowCount) implements TreasureTableDefinition {
    public DragonTreasureDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredData, "authoredData");
    }

    @Override
    public TreasureTableKind kind() {
        return TreasureTableKind.DRAGON;
    }

    @Override
    public int tableCount() {
        return 3;
    }

    @Override
    public int rowCount() {
        return authoredData.gems().typeTable().size()
                + authoredData.artObjects().typeTable().size()
                + authoredData.magicItems().stream().mapToInt(group -> group.typeTable().size()).sum();
    }

    @Override
    public String category() {
        return name;
    }
}
