package com.thedndpub.data.dto.nametable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record NameTableSummaryDto(
        NameTableId id,
        String name,
        SourceIdentity source,
        Integer page,
        int tableCount,
        int rowCount) {
    public static NameTableSummaryDto from(NameTableDefinition definition) {
        return new NameTableSummaryDto(
                definition.id(),
                definition.name(),
                definition.source().identity(),
                definition.source().page(),
                definition.tables().size(),
                definition.rowCount());
    }
}
