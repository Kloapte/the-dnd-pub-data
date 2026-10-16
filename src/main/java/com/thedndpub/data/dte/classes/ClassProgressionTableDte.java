package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassProgressionTableDte(
        String title,
        List<String> colLabels,
        List<List<ClassTableCellDte>> rows,
        List<List<Integer>> rowsSpellProgression,
        List<ClassTableSubclassDte> subclasses) {
    public ClassProgressionTableDte {
        colLabels = ClassDteCollections.list(colLabels);
        rows = ClassDteCollections.rows(rows);
        rowsSpellProgression = ClassDteCollections.rows(rowsSpellProgression);
        subclasses = ClassDteCollections.list(subclasses);
    }
}
