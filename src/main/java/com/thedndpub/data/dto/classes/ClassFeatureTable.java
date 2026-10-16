package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureTable(
        String caption,
        List<String> columns,
        List<String> columnStyles,
        List<ClassTableRow> rows,
        List<String> footnotes) implements ClassFeatureNode {
    public ClassFeatureTable {
        columns = columns == null ? List.of() : List.copyOf(columns);
        columnStyles = columnStyles == null ? List.of() : List.copyOf(columnStyles);
        rows = rows == null ? List.of() : List.copyOf(rows);
        footnotes = footnotes == null ? List.of() : List.copyOf(footnotes);
    }
}
