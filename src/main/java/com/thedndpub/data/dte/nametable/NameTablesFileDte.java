package com.thedndpub.data.dte.nametable;

import java.util.List;

public record NameTablesFileDte(List<NameTableDte> name) {
    public NameTablesFileDte {
        name = name == null ? null : List.copyOf(name);
    }
}
