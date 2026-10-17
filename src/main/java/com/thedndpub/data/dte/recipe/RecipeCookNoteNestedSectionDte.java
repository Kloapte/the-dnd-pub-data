package com.thedndpub.data.dte.recipe;

import java.util.List;

public record RecipeCookNoteNestedSectionDte(String type, String name, String source, List<String> entries) {
    public RecipeCookNoteNestedSectionDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
