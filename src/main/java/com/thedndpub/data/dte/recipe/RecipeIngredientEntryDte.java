package com.thedndpub.data.dte.recipe;

public sealed interface RecipeIngredientEntryDte permits RecipeIngredientTextDte,
        RecipeIngredientAmountDte, RecipeIngredientGroupDte {
}
