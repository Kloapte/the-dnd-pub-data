package com.thedndpub.data.dte.common.entry;

import java.util.List;

/**
 * A table cell carrying recursive formatted content and optional source label.
 */
public record StructuredTableCellDte(String type, String name, List<EntryDte> entries) implements EntryTableCellDte {
}
