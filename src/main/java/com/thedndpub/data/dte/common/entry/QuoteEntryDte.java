package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record QuoteEntryDte(String by, List<EntryDte> entries) implements EntryDte {
}
