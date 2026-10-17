package com.thedndpub.data.dte.common.entry;

import java.util.List;

/** Typed insetReadaloud node used for internal descriptive source content. */
public record ReadaloudEntryDte(String name, List<EntryDte> entries) implements EntryDte {
    public ReadaloudEntryDte {
        entries = entries == null ? List.of() : List.copyOf(entries);
    }
}
