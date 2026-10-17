package com.thedndpub.data.dte.recipe;

public final class RecipeInstructionEntryDeserializer extends RecipeDteVariantDeserializer<RecipeInstructionEntryDte> {
    @Override
    protected Class<? extends RecipeInstructionEntryDte> typeForDiscriminator(String discriminator) {
        return "entries".equals(discriminator) ? RecipeInstructionSectionDte.class : null;
    }

    @Override
    protected RecipeInstructionEntryDte textValue(String value) {
        return new RecipeInstructionTextDte(value);
    }
}
