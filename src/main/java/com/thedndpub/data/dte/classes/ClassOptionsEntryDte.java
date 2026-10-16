package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassOptionsEntryDte(
        Integer count,
        String style,
        List<ClassEntryDte> entries) implements ClassEntryDte {
    public ClassOptionsEntryDte {
        entries = ClassDteCollections.list(entries);
    }
}
