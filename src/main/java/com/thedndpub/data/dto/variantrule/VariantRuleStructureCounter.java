package com.thedndpub.data.dto.variantrule;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryTableCellDte;
import com.thedndpub.data.dte.common.entry.InlineEntryDte;
import com.thedndpub.data.dte.common.entry.InsetEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.QuoteEntryDte;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.SectionEntryDte;
import com.thedndpub.data.dte.common.entry.StructuredTableCellDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;

import java.util.List;

final class VariantRuleStructureCounter {
    private VariantRuleStructureCounter() {
    }

    static VariantRuleStructureCounts count(List<EntryDte> entries) {
        Counter counter = new Counter();
        visitAll(entries, counter);
        return new VariantRuleStructureCounts(counter.sections, counter.tables);
    }

    private static void visitAll(List<EntryDte> entries, Counter counter) {
        if (entries == null) {
            return;
        }
        entries.forEach(entry -> visit(entry, counter));
    }

    private static void visit(EntryDte entry, Counter counter) {
        if (entry instanceof NamedEntryDte named) {
            visitAll(named.entries(), counter);
        } else if (entry instanceof ListEntryDte list) {
            visitAll(list.items(), counter);
        } else if (entry instanceof ListItemEntryDte item) {
            visitAll(item.entries(), counter);
        } else if (entry instanceof TableEntryDte table) {
            counter.tables++;
            visitTableCells(table.rows(), counter);
        } else if (entry instanceof QuoteEntryDte quote) {
            visitAll(quote.entries(), counter);
        } else if (entry instanceof InsetEntryDte inset) {
            visitAll(inset.entries(), counter);
        } else if (entry instanceof SectionEntryDte section) {
            counter.sections++;
            visitAll(section.entries(), counter);
        } else if (entry instanceof InlineEntryDte inline) {
            visitAll(inline.entries(), counter);
        }
    }

    private static void visitTableCells(List<List<EntryTableCellDte>> rows, Counter counter) {
        if (rows == null) {
            return;
        }
        for (List<EntryTableCellDte> row : rows) {
            for (EntryTableCellDte cell : row) {
                if (cell instanceof StructuredTableCellDte structured) {
                    visitAll(structured.entries(), counter);
                } else if (cell instanceof RollTableCellDte roll) {
                    visit(roll.entry(), counter);
                }
            }
        }
    }

    private static final class Counter {
        private int sections;
        private int tables;
    }
}
