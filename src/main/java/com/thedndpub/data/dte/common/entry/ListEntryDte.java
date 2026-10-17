package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record ListEntryDte(String style, Integer columns, List<EntryDte> items, String name) implements EntryDte {
    public ListEntryDte(String style, Integer columns, List<EntryDte> items) {
        this(style, columns, items, null);
    }
}
