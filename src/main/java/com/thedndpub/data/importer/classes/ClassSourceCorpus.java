package com.thedndpub.data.importer.classes;

import java.util.List;

public record ClassSourceCorpus(List<ClassSourceDocument> documents) {
    public ClassSourceCorpus {
        documents = List.copyOf(documents);
    }

    public List<ClassSourceRecord> records() {
        return documents.stream().flatMap(document -> document.records().stream()).toList();
    }
}
