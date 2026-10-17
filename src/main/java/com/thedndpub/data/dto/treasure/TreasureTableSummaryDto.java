package com.thedndpub.data.dto.treasure;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TreasureTableSummaryDto(
        TreasureTableId id,
        String name,
        SourceIdentity source,
        Integer page,
        String collectionType,
        String category,
        int tableCount,
        int rowCount) {
    public static TreasureTableSummaryDto from(TreasureTableDefinition definition) {
        return new TreasureTableSummaryDto(definition.id(), definition.name(), definition.source().identity(),
                definition.source().page(), definition.kind().sourceCollection(), definition.category(),
                definition.tableCount(), definition.rowCount());
    }
}
