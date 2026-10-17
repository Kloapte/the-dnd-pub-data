package com.thedndpub.data.dte.recipe;

import java.util.List;

public record RecipeCookNoteSectionDte(String type, List<RecipeCookNoteNestedSectionDte> entries)
        implements RecipeCookNoteEntryDte {
    public RecipeCookNoteSectionDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
