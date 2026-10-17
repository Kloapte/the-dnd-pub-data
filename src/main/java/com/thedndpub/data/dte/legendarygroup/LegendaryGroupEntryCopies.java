package com.thedndpub.data.dte.legendarygroup;

import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.common.entry.EntryGeneratedTablesDte;
import com.thedndpub.data.dte.common.entry.EntryTableMetadataDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.ReadaloudEntryDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.common.entry.TextTableCellDte;

import java.util.List;

/** Deep immutable snapshots of the shared entry variants observed in legendary-group support. */
public final class LegendaryGroupEntryCopies {
    private LegendaryGroupEntryCopies() {
    }

    public static List<EntryDte> copy(List<EntryDte> entries) {
        return entries == null ? null : entries.stream().map(LegendaryGroupEntryCopies::copy).toList();
    }

    public static EntryDte copy(EntryDte entry) {
        return switch (entry) {
            case TextEntryDte text -> text;
            case NamedEntryDte named -> new NamedEntryDte(
                    named.type(), named.name(), named.entry(), copy(named.entries()),
                    named.source(), named.page(), named.data(), named.id());
            case ListEntryDte list -> new ListEntryDte(
                    list.style(), list.columns(), copy(list.items()), list.name());
            case ListItemEntryDte item -> new ListItemEntryDte(
                    item.name(), item.entry(), copy(item.entries()));
            case ReadaloudEntryDte readaloud -> new ReadaloudEntryDte(
                    readaloud.name(), copy(readaloud.entries()));
            case TableEntryDte table -> copyTable(table);
            default -> throw new IllegalArgumentException(
                    "Unsupported legendary-group entry variant: " + entry.getClass().getSimpleName());
        };
    }

    private static TableEntryDte copyTable(TableEntryDte table) {
        var rows = table.rows() == null ? null : table.rows().stream().map(row -> row.stream().map(cell -> {
            if (!(cell instanceof TextTableCellDte)) {
                throw new IllegalArgumentException("Unsupported legendary-group table cell: " + cell);
            }
            return cell;
        }).toList()).toList();
        EntryTableMetadataDte metadata = table.data();
        if (metadata != null && metadata.genTables() != null) {
            var generated = metadata.genTables();
            metadata = new EntryTableMetadataDte(new EntryGeneratedTablesDte(
                    immutable(generated.reprintedAs()), generated.tableInclude(), generated.tableName()));
        }
        return new TableEntryDte(
                table.caption(), immutable(table.colLabels()), immutable(table.colStyles()), rows,
                table.source(), table.page(), immutable(table.footnotes()), metadata,
                table.srd52(), table.isNameGenerator());
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
