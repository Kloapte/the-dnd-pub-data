package com.thedndpub.data.dte.book;

import java.util.List;

public record BooksFileDte(List<BookDte> book) {
    public BooksFileDte {
        book = book == null ? List.of() : List.copyOf(book);
    }
}
