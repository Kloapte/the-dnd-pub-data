package com.thedndpub.data.dto.book;

import com.thedndpub.data.dte.book.BookContentDte;

import java.util.List;

public record PublicationImportProvenance(
        String sourceId,
        String coverType,
        String coverPath,
        String coverCredit,
        List<BookContentDte> contents) {
    public PublicationImportProvenance {
        contents = List.copyOf(contents);
    }
}
