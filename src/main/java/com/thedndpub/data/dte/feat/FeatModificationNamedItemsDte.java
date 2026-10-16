package com.thedndpub.data.dte.feat;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record FeatModificationNamedItemsDte(String name, List<EntryDte> entries) implements FeatModificationItemsDte {
}
