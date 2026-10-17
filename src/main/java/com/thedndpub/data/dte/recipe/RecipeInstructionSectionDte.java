package com.thedndpub.data.dte.recipe;

import java.util.List;

public record RecipeInstructionSectionDte(String type, String name, List<String> entries, Integer page)
        implements RecipeInstructionEntryDte {
    public RecipeInstructionSectionDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
