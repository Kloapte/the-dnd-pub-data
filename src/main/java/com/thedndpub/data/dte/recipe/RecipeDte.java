package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

public record RecipeDte(
        String name,
        List<String> alias,
        String source,
        Integer page,
        String type,
        List<String> dishTypes,
        String diet,
        List<String> allergenGroups,
        @JsonDeserialize(using = RecipeMakesDeserializer.class) RecipeMakesDte makes,
        RecipeServesDte serves,
        @JsonDeserialize(contentUsing = RecipeIngredientEntryDeserializer.class)
        List<RecipeIngredientEntryDte> ingredients,
        @JsonDeserialize(contentUsing = RecipeEquipmentEntryDeserializer.class)
        List<RecipeEquipmentEntryDte> equipment,
        @JsonDeserialize(contentUsing = RecipeInstructionEntryDeserializer.class)
        List<RecipeInstructionEntryDte> instructions,
        @JsonDeserialize(contentUsing = RecipeCookNoteEntryDeserializer.class)
        List<RecipeCookNoteEntryDte> noteCook,
        List<String> miscTags,
        Boolean hasFluff,
        Boolean hasFluffImages,
        List<RecipeSourceReferenceDte> otherSources) {
    public RecipeDte {
        alias = copy(alias);
        dishTypes = copy(dishTypes);
        allergenGroups = copy(allergenGroups);
        ingredients = copy(ingredients);
        equipment = copy(equipment);
        instructions = copy(instructions);
        noteCook = copy(noteCook);
        miscTags = copy(miscTags);
        otherSources = copy(otherSources);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
