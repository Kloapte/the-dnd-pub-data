package com.thedndpub.data.dto.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RecipeSummaryDto(
        RecipeId id,
        String name,
        SourceIdentity source,
        Integer page,
        String type,
        List<String> dishTypes) {
    public RecipeSummaryDto {
        dishTypes = dishTypes == null ? null : List.copyOf(dishTypes);
    }
}
