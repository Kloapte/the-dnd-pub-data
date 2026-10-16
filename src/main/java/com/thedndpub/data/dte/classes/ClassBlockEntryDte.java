package com.thedndpub.data.dte.classes;

import java.util.List;

public record ClassBlockEntryDte(
        String type,
        String name,
        String entry,
        List<ClassEntryDte> entries,
        List<ClassEntryDte> items,
        String style,
        Integer columns,
        String source,
        Integer page) implements ClassEntryDte {
    public ClassBlockEntryDte {
        entries = ClassDteCollections.list(entries);
        items = ClassDteCollections.list(items);
    }
}
