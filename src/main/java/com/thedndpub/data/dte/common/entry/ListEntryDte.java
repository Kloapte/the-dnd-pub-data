package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record ListEntryDte(String style, Integer columns, List<EntryDte> items) implements EntryDte {
}
