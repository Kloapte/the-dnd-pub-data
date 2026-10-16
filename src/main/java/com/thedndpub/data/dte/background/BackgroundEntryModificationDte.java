package com.thedndpub.data.dte.background;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record BackgroundEntryModificationDte(
        String mode,
        Integer index,
        String replaceName,
        List<EntryDte> items) {
    public BackgroundEntryModificationDte {
        items = List.copyOf(items);
    }
}
