package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

public record RecipeIngredientTextDte(@JsonValue String value)
        implements RecipeIngredientEntryDte, RecipeIngredientLeafDte {
}
