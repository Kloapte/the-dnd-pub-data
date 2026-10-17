package com.thedndpub.data.dto.nametable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record NameTableDto(
        NameTableId id,
        String name,
        SourceReference source,
        int tableCount,
        int rowCount) {
    public static NameTableDto from(NameTableDefinition definition) {
        return new NameTableDto(
                definition.id(),
                definition.name(),
                definition.source(),
                definition.tables().size(),
                definition.rowCount());
    }
}
