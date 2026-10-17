package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record InlineEntryDte(List<EntryDte> entries) implements EntryDte {
    public InlineEntryDte {
        entries = entries == null ? List.of() : List.copyOf(entries);
    }
}
