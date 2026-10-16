package com.thedndpub.data.dto.background;

import com.thedndpub.data.dte.background.BackgroundDte;

import java.util.List;

public record ResolvedBackgroundDte(
        BackgroundDte root,
        BackgroundDte effective,
        List<com.thedndpub.data.dte.common.entry.EntryDte> entries,
        List<BackgroundId> copyLineage) {
    public ResolvedBackgroundDte {
        entries = List.copyOf(entries);
        copyLineage = List.copyOf(copyLineage);
    }
}
