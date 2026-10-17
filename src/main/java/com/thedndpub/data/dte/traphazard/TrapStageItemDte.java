package com.thedndpub.data.dte.traphazard;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record TrapStageItemDte(String type, String name, String entry, List<EntryDte> entries) {
    public TrapStageItemDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
