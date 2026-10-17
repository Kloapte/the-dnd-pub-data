package com.thedndpub.data.dto.treasure;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TreasureTableStructureDto(
        String role,
        int rowCount,
        List<TreasureRollRangeDto> rollRanges,
        List<TreasureTableStructureDto> nestedTables) {
    public TreasureTableStructureDto {
        rollRanges = rollRanges == null ? List.of() : List.copyOf(rollRanges);
        nestedTables = nestedTables == null ? List.of() : List.copyOf(nestedTables);
    }
}
