package com.thedndpub.data.dte.cultboon;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record BoonDte(
        String name,
        String source,
        Integer page,
        String type,
        List<EntryDte> entries,
        List<CultBoonReprintDte> reprintedAs,
        String abilityEntry,
        CultTextDte signatureSpells) {
    public BoonDte {
        entries = entries == null ? null : List.copyOf(entries);
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
    }
}
