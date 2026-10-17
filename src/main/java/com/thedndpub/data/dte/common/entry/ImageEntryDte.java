package com.thedndpub.data.dte.common.entry;

public record ImageEntryDte(
        EntryHrefDte href,
        String title,
        Integer width,
        Integer height,
        String credit) implements EntryDte {
}
