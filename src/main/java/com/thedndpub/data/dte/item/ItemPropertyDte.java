package com.thedndpub.data.dte.item;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ItemPropertyDte(
        String name,
        String abbreviation,
        String source,
        Integer page,
        List<ItemReprintDte> reprintedAs,
        String template,
        List<EntryDte> entries,
        Boolean basicRules2024) {
}
