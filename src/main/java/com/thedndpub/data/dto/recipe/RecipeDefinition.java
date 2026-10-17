package com.thedndpub.data.dto.recipe;

import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;
import java.util.Objects;

public record RecipeDefinition(
        RecipeId id,
        String name,
        SourceReference source,
        String type,
        List<String> dishTypes,
        RecipeSourceContent authoredContent) {
    public RecipeDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(authoredContent, "authoredContent");
        dishTypes = dishTypes == null ? null : List.copyOf(dishTypes);
    }
}
