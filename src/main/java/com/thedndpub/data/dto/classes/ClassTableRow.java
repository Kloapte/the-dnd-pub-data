package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassTableRow(
        Integer level,
        List<ClassTableCell> cells) {
    public ClassTableRow {
        cells = cells == null ? List.of() : List.copyOf(cells);
    }
}
