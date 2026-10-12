package com.thedndpub.data.dte.common.entry;

import java.util.List;

/**
 * A named list item; 5eTools uses item as a type while itemSub is retained by NamedEntryDte.
 */
public record ListItemEntryDte(String name, String entry, List<EntryDte> entries) implements EntryDte {
}
