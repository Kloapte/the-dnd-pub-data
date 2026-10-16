package com.thedndpub.data.dto.item;

import com.thedndpub.data.dte.item.ItemSourceDte;

import java.util.List;
import com.thedndpub.data.dte.item.ItemPropertyReferenceDte;
import com.thedndpub.data.dte.common.entry.EntryDte;

public record ResolvedItemDte(
        ItemSourceDte source,
        ResolvedItemDte copiedFrom,
        boolean copyResolved,
        List<ItemPropertyReferenceDte> resolvedProperties,
        List<EntryDte> resolvedEntries) {
    public ResolvedItemDte {
        resolvedProperties = List.copyOf(resolvedProperties);
        resolvedEntries = List.copyOf(resolvedEntries);
    }
}
