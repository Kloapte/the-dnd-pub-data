package com.thedndpub.data.dte.cultboon;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record CultDte(
        String name,
        String source,
        Integer page,
        String type,
        List<EntryDte> entries,
        List<CultBoonSourceReferenceDte> otherSources,
        CultTextDte goal,
        CultTextDte cultists,
        CultTextDte signatureSpells,
        List<CultBoonReprintDte> reprintedAs) {
    public CultDte {
        entries = entries == null ? null : List.copyOf(entries);
        otherSources = otherSources == null ? null : List.copyOf(otherSources);
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
    }
}
