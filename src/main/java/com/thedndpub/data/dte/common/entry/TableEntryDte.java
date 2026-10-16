package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record TableEntryDte(String caption, List<String> colLabels, List<String> colStyles,
                            List<List<EntryTableCellDte>> rows, String source, Integer page,
                            List<String> footnotes, EntryTableMetadataDte data, Boolean srd52) implements EntryDte {
}
