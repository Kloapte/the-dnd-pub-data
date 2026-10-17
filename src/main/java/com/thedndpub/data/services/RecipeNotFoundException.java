package com.thedndpub.data.services;

public final class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(String id) {
        super("Recipe not found: " + id);
    }
}
