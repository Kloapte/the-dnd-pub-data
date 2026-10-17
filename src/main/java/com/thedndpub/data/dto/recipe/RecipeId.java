package com.thedndpub.data.dto.recipe;

import com.fasterxml.jackson.annotation.JsonValue;

public record RecipeId(@JsonValue String value) {
    public RecipeId {
        if (value == null || !value.startsWith("recipe:")) {
            throw new IllegalArgumentException("Recipe ID must start with recipe:");
        }
    }
}
