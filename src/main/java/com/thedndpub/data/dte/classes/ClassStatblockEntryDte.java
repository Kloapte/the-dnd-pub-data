package com.thedndpub.data.dte.classes;

public record ClassStatblockEntryDte(
        String tag,
        String name,
        String source,
        Integer page,
        String style) implements ClassEntryDte {
}
