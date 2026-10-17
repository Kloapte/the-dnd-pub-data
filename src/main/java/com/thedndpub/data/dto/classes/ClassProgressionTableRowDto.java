package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassProgressionTableRowDto(
        Integer level,
        List<ClassTableCell> cells,
        List<ClassPositionedTableCellDto> positionedCells) {
    public ClassProgressionTableRowDto {
        cells = cells == null ? List.of() : List.copyOf(cells);
        positionedCells = positionedCells == null ? List.of() : List.copyOf(positionedCells);
    }
}
