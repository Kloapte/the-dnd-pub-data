package com.thedndpub.data.dto.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RecipeDetailDto(
        RecipeId id,
        String name,
        SourceIdentity source,
        Integer page,
        String type,
        List<String> dishTypes,
        int ingredientEntryCount,
        int equipmentEntryCount,
        int instructionEntryCount,
        int cookNoteEntryCount,
        int groupedIngredientSectionCount,
        int instructionSectionCount) {
    public RecipeDetailDto {
        dishTypes = dishTypes == null ? null : List.copyOf(dishTypes);
    }
}
