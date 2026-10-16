package com.thedndpub.data.dte.book;

import java.util.List;

public record BookContentDte(String name, BookOrdinalDte ordinal, List<BookHeaderDte> headers) {
    public BookContentDte {
        headers = headers == null ? List.of() : List.copyOf(headers);
    }
}
