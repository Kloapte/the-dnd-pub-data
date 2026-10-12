package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record ListEntryDte(String style, List<EntryDte> items) implements EntryDte {
}
