package com.thedndpub.data.dte.common.entry;

import java.util.List;

public record TableEntryDte(String caption, List<String> colLabels, List<String> colStyles,
                            List<List<EntryTableCellDte>> rows, String source, Integer page) implements EntryDte {
}
