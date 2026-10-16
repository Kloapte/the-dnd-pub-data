package com.thedndpub.data.dte.race;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record RaceEntryModificationDte(
        String mode,
        String replace,
        List<String> names,
        String with,
        String flags,
        List<EntryDte> items) {
    public RaceEntryModificationDte {
        names = names == null ? List.of() : List.copyOf(names);
        items = items == null ? List.of() : List.copyOf(items);
    }
}
