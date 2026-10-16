package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassTableEntryDte(
        String caption,
        List<String> colLabels,
        List<String> colStyles,
        List<List<ClassTableCellDte>> rows,
        Integer page,
        List<String> footnotes,
        ClassEntryMetadataDte data) implements ClassEntryDte {
    public ClassTableEntryDte {
        colLabels = ClassDteCollections.list(colLabels);
        colStyles = ClassDteCollections.list(colStyles);
        rows = ClassDteCollections.rows(rows);
        footnotes = ClassDteCollections.list(footnotes);
    }
}
