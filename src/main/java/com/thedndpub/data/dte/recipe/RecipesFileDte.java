package com.thedndpub.data.dte.recipe;

import java.util.List;

public record RecipesFileDte(List<RecipeDte> recipe) {
    public RecipesFileDte {
        recipe = recipe == null ? null : List.copyOf(recipe);
    }
}
