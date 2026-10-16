package com.thedndpub.data.dte.book;

public record StructuredBookHeaderDte(
        String header,
        Integer depth,
        Integer index,
        String source,
        Boolean statblock,
        String tag,
        String uid) implements BookHeaderDte {
}
