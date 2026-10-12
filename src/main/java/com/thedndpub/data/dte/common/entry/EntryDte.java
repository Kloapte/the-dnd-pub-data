package com.thedndpub.data.dte.common.entry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EntryDeserializer.class)
public sealed interface EntryDte permits TextEntryDte, NamedEntryDte, ListEntryDte,
        ListItemEntryDte, TableEntryDte, QuoteEntryDte, InsetEntryDte {
}
