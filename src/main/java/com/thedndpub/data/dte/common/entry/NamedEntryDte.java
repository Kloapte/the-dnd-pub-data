package com.thedndpub.data.dte.common.entry;

import java.util.List;

/**
 * Named recursive prose entry; retains the source type (entries/itemSub/variantSub/variantInner).
 */
public record NamedEntryDte(String type, String name, String entry, List<EntryDte> entries,
                            String source, Integer page) implements EntryDte {
}
