package com.thedndpub.data.dte.condition;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record DiseaseDte(
        String name,
        String source,
        Integer page,
        List<String> reprintedAs,
        List<EntryDte> entries,
        String type,
        Boolean srd,
        Boolean srd52) {
}
