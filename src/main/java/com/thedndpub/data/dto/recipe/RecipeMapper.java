package com.thedndpub.data.dto.recipe;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.recipe.RecipeIngredientGroupDte;
import com.thedndpub.data.dte.recipe.RecipeInstructionSectionDte;
import com.thedndpub.data.dte.recipe.RecipeDte;
import org.springframework.stereotype.Component;

@Component
public final class RecipeMapper {
    public RecipeDefinition normalize(RecipeDte recipe, String id) {
        RecipeSourceContent content = new RecipeSourceContent(
                recipe.alias(),
                recipe.diet(),
                recipe.allergenGroups(),
                recipe.makes(),
                recipe.serves(),
                recipe.ingredients(),
                recipe.equipment(),
                recipe.instructions(),
                recipe.noteCook(),
                recipe.miscTags(),
                recipe.hasFluff(),
                recipe.hasFluffImages(),
                recipe.otherSources());
        return new RecipeDefinition(
                new RecipeId(id),
                recipe.name(),
                new SourceReference(new SourceIdentity(recipe.source()), recipe.page()),
                recipe.type(),
                recipe.dishTypes(),
                content);
    }

    public RecipeSummaryDto summary(RecipeDefinition recipe) {
        return new RecipeSummaryDto(
                recipe.id(), recipe.name(), recipe.source().identity(), recipe.source().page(),
                recipe.type(), recipe.dishTypes());
    }

    public RecipeDetailDto detail(RecipeDefinition recipe) {
        RecipeSourceContent content = recipe.authoredContent();
        return new RecipeDetailDto(
                recipe.id(), recipe.name(), recipe.source().identity(), recipe.source().page(),
                recipe.type(), recipe.dishTypes(),
                content.ingredients().size(), countEquipment(content), content.instructions().size(),
                content.cookNotes() == null ? 0 : content.cookNotes().size(),
                countIngredientGroups(content), countInstructionSections(content));
    }

    private static int countEquipment(RecipeSourceContent content) {
        return content.equipment() == null ? 0 : content.equipment().size();
    }

    private static int countIngredientGroups(RecipeSourceContent content) {
        return content.ingredients().stream()
                .filter(RecipeIngredientGroupDte.class::isInstance)
                .mapToInt(entry -> 1)
                .sum();
    }

    private static int countInstructionSections(RecipeSourceContent content) {
        return (int) content.instructions().stream()
                .filter(RecipeInstructionSectionDte.class::isInstance)
                .count();
    }
}
