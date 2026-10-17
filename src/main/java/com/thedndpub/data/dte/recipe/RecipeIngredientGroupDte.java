package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record RecipeIngredientGroupDte(
        String type,
        String name,
        @JsonDeserialize(contentUsing = RecipeIngredientLeafDeserializer.class)
        List<RecipeIngredientLeafDte> entries,
        Integer page) implements RecipeIngredientEntryDte {
    public RecipeIngredientGroupDte {
        entries = entries == null ? null : List.copyOf(entries);
    }
}
