package com.thedndpub.data.dte.item;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ItemTypeAdditionalEntriesDte(
        String name,
        String source,
        String appliesTo,
        List<EntryDte> entries) {
}
