package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassProgressionTableDto(
        int order,
        ClassTableKind kind,
        String title,
        List<String> columns,
        List<ClassProgressionTableRowDto> rows,
        List<ClassTableSubclassSelector> subclasses) {
    public ClassProgressionTableDto {
        columns = columns == null ? List.of() : List.copyOf(columns);
        rows = rows == null ? List.of() : List.copyOf(rows);
        subclasses = subclasses == null ? List.of() : List.copyOf(subclasses);
    }
}
