package com.thedndpub.data.importer.classes;

import com.thedndpub.data.dte.classes.ClassesFileDte;

import java.util.List;

public record ClassSourceDocument(
        String resource,
        ClassesFileDte data,
        List<ClassSourceRecord> records) {
    public ClassSourceDocument {
        records = List.copyOf(records);
    }
}
