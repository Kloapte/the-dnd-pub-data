package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record InsetEntryDte(String name, List<EntryDte> entries, String source, Integer page) implements EntryDte {
}
