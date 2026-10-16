package com.thedndpub.data.dte.item;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ItemEntryTemplateDte(String name, String source, List<EntryDte> entriesTemplate) {
}
