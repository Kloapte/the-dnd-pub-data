package com.thedndpub.data.dto.recipe;

import com.thedndpub.data.dte.recipe.RecipeCookNoteEntryDte;
import com.thedndpub.data.dte.recipe.RecipeEquipmentEntryDte;
import com.thedndpub.data.dte.recipe.RecipeIngredientEntryDte;
import com.thedndpub.data.dte.recipe.RecipeInstructionEntryDte;
import com.thedndpub.data.dte.recipe.RecipeMakesDte;
import com.thedndpub.data.dte.recipe.RecipeServesDte;
import com.thedndpub.data.dte.recipe.RecipeSourceReferenceDte;

import java.util.List;

/** Complete typed recipe content retained internally and omitted from public DTOs. */
public record RecipeSourceContent(
        List<String> aliases,
        String diet,
        List<String> allergenGroups,
        RecipeMakesDte makes,
        RecipeServesDte serves,
        List<RecipeIngredientEntryDte> ingredients,
        List<RecipeEquipmentEntryDte> equipment,
        List<RecipeInstructionEntryDte> instructions,
        List<RecipeCookNoteEntryDte> cookNotes,
        List<String> miscTags,
        Boolean hasFluff,
        Boolean hasFluffImages,
        List<RecipeSourceReferenceDte> otherSources) {
    public RecipeSourceContent {
        aliases = copy(aliases);
        allergenGroups = copy(allergenGroups);
        ingredients = copy(ingredients);
        equipment = copy(equipment);
        instructions = copy(instructions);
        cookNotes = copy(cookNotes);
        miscTags = copy(miscTags);
        otherSources = copy(otherSources);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
