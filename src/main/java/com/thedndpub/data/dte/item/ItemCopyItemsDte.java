package com.thedndpub.data.dte.item;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record ItemCopyItemsDte(String text, List<EntryDte> entries) {
}
