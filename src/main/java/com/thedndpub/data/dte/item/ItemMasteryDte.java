package com.thedndpub.data.dte.item;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ItemMasteryDte(
        String name,
        String source,
        Integer page,
        Boolean srd52,
        Boolean basicRules2024,
        List<EntryDte> entries) {
}
