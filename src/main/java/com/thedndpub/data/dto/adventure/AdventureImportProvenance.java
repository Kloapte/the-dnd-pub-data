package com.thedndpub.data.dto.adventure;

import com.thedndpub.data.dte.book.BookContentDte;
import com.thedndpub.data.dte.book.BookCoverDte;

import java.util.List;

public record AdventureImportProvenance(
        String resourceName,
        BookCoverDte cover,
        List<BookContentDte> contents) {
    public AdventureImportProvenance {
        contents = List.copyOf(contents);
    }
}
