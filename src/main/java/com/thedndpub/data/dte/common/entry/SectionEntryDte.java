package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record SectionEntryDte(String name, String id, List<EntryDte> entries) implements EntryDte {
    public SectionEntryDte(String name, List<EntryDte> entries) {
        this(name, null, entries);
    }

    public SectionEntryDte(List<EntryDte> entries) {
        this(null, null, entries);
    }
}
