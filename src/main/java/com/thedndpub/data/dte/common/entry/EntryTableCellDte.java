package com.thedndpub.data.dte.common.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EntryTableCellDeserializer.class)
public sealed interface EntryTableCellDte permits TextTableCellDte, RollTableCellDte, StructuredTableCellDte,
        NumericTableCellDte {
}
