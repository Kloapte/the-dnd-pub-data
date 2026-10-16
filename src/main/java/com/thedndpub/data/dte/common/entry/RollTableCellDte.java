package com.thedndpub.data.dte.common.entry;

public record RollTableCellDte(String type, TableRollDte roll, EntryDte entry) implements EntryTableCellDte {
}
