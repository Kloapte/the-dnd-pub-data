package com.thedndpub.data.dte.book;

import java.util.List;

public record BookDte(
        String name,
        String id,
        String source,
        String group,
        BookCoverDte cover,
        String coverCredit,
        String published,
        String author,
        List<BookContentDte> contents,
        String revised,
        List<String> alias,
        String parentSource) {
    public BookDte {
        contents = contents == null ? List.of() : List.copyOf(contents);
        alias = alias == null ? List.of() : List.copyOf(alias);
    }
}
