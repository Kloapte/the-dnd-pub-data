package com.thedndpub.data.dte.object;

import com.thedndpub.data.dte.common.entry.EntryDte;
import java.util.List;

public record ObjectActionItemDte(String type, String name, List<EntryDte> entries) {
    public ObjectActionItemDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
