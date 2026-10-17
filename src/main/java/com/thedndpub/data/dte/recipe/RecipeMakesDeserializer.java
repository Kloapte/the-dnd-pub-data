package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class RecipeMakesDeserializer extends JsonDeserializer<RecipeMakesDte> {
    @Override
    public RecipeMakesDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new RecipeMakesTextDte(parser.getText());
        }
        if (parser.currentToken() == JsonToken.VALUE_NUMBER_INT
                || parser.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
            return new RecipeMakesQuantityDte(parser.getDecimalValue());
        }
        throw JsonMappingException.from(parser, "Recipe makes must be an authored string or number");
    }
}
