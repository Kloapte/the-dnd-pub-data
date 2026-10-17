package com.thedndpub.data.dte.adventure;

import com.thedndpub.data.dte.book.BookContentDte;
import com.thedndpub.data.dte.book.BookCoverDte;

import java.util.List;

public record AdventureDte(
        String name,
        String id,
        String source,
        String group,
        BookCoverDte cover,
        String published,
        String author,
        List<BookContentDte> contents,
        List<String> alias,
        AdventureLevelDte level,
        String parentSource,
        Integer publishedOrder,
        String storyline) {
    public AdventureDte {
        contents = contents == null ? null : List.copyOf(contents);
        alias = alias == null ? null : List.copyOf(alias);
    }
}
