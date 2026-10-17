package com.thedndpub.data.dte.treasure;

import java.util.List;

public record ArtObjectTableDte(
        String name,
        String source,
        Integer page,
        Integer type,
        List<String> table,
        List<String> reprintedAs) {
    public ArtObjectTableDte {
        table = table == null ? null : List.copyOf(table);
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
    }
}
