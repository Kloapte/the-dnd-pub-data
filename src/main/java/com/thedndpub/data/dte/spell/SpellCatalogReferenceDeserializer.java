package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

public final class SpellCatalogReferenceDeserializer extends JsonDeserializer<SpellCatalogReferenceDte> {
    @Override
    public SpellCatalogReferenceDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_TRUE || parser.currentToken() == JsonToken.VALUE_FALSE) {
            return new SpellCatalogReferenceDte(parser.getBooleanValue(), null);
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new SpellCatalogReferenceDte(null, parser.getText());
        }
        throw MismatchedInputException.from(parser, SpellCatalogReferenceDte.class,
                "spell.srd and spell.srd52 must be boolean or string; got " + parser.currentToken());
    }
}
