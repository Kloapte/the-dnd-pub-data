package com.thedndpub.data.dte.recipe;

public final class RecipeIngredientEntryDeserializer extends RecipeDteVariantDeserializer<RecipeIngredientEntryDte> {
    @Override
    protected Class<? extends RecipeIngredientEntryDte> typeForDiscriminator(String discriminator) {
        return switch (discriminator == null ? "" : discriminator) {
            case "ingredient" -> RecipeIngredientAmountDte.class;
            case "entries" -> RecipeIngredientGroupDte.class;
            default -> null;
        };
    }

    @Override
    protected RecipeIngredientEntryDte textValue(String value) {
        return new RecipeIngredientTextDte(value);
    }
}
