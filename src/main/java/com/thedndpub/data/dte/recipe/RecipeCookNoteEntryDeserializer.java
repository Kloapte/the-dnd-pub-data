package com.thedndpub.data.dte.recipe;

public final class RecipeCookNoteEntryDeserializer extends RecipeDteVariantDeserializer<RecipeCookNoteEntryDte> {
    @Override
    protected Class<? extends RecipeCookNoteEntryDte> typeForDiscriminator(String discriminator) {
        return "entries".equals(discriminator) ? RecipeCookNoteSectionDte.class : null;
    }

    @Override
    protected RecipeCookNoteEntryDte textValue(String value) {
        return new RecipeCookNoteTextDte(value);
    }
}
