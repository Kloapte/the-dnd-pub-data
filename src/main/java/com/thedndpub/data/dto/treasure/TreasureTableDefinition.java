package com.thedndpub.data.dto.treasure;

import com.thedndpub.data.dto.source.SourceReference;

public sealed interface TreasureTableDefinition permits IndividualTreasureDefinition,
        TreasureHoardDefinition, DragonTreasureDefinition, GemTableDefinition,
        ArtObjectTableDefinition, MagicItemTableDefinition {
    TreasureTableId id();

    String name();

    SourceReference source();

    TreasureTableKind kind();

    int tableCount();

    int rowCount();

    String category();
}
