package com.thedndpub.data.dte.recipe;

public final class RecipeEquipmentEntryDeserializer extends RecipeDteVariantDeserializer<RecipeEquipmentEntryDte> {
    @Override
    protected Class<? extends RecipeEquipmentEntryDte> typeForDiscriminator(String discriminator) {
        return "ingredient".equals(discriminator) ? RecipeEquipmentAmountDte.class : null;
    }

    @Override
    protected RecipeEquipmentEntryDte textValue(String value) {
        return new RecipeEquipmentTextDte(value);
    }
}
