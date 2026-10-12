package com.thedndpub.data.dte.common.entry;

/**
 * Plain text is retained byte-for-byte at the JSON string level, including 5eTools inline tags.
 */
public record TextEntryDte(String text) implements EntryDte {
}
