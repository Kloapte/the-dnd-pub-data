package com.thedndpub.data.dte.legendarygroup;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;
import java.util.Objects;

public record LegendaryGroupArrayModificationDte(
        LegendaryGroupArrayMode mode,
        List<EntryDte> items) {
    public LegendaryGroupArrayModificationDte {
        Objects.requireNonNull(mode, "mode");
        Objects.requireNonNull(items, "items");
        items = LegendaryGroupEntryCopies.copy(items);
    }
}
