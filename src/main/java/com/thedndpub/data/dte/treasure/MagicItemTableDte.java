package com.thedndpub.data.dte.treasure;

import java.util.List;

public record MagicItemTableDte(
        String name,
        String source,
        Integer page,
        String type,
        List<MagicItemTableRowDte> table) {
    public MagicItemTableDte {
        table = table == null ? null : List.copyOf(table);
    }
}
