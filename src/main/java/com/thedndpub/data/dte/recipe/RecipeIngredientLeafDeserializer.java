package com.thedndpub.data.dte.recipe;

public final class RecipeIngredientLeafDeserializer extends RecipeDteVariantDeserializer<RecipeIngredientLeafDte> {
    @Override
    protected Class<? extends RecipeIngredientLeafDte> typeForDiscriminator(String discriminator) {
        return "ingredient".equals(discriminator) ? RecipeIngredientAmountDte.class : null;
    }

    @Override
    protected RecipeIngredientLeafDte textValue(String value) {
        return new RecipeIngredientTextDte(value);
    }
}
