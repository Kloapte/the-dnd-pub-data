package com.thedndpub.data.dte.nametable;

import java.util.List;

public record NameTableDte(String name, String source, Integer page, List<NameTableOptionDte> tables) {
    public NameTableDte {
        tables = tables == null ? null : List.copyOf(tables);
    }
}
