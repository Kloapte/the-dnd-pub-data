package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class ItemNumberTextDeserializer extends JsonDeserializer<ItemNumberTextDte> {
    @Override
    public ItemNumberTextDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isNumeric()) {
            return new ItemNumberTextDte(parser.getIntValue(), null);
        }
        if (parser.currentToken().isScalarValue()) {
            return new ItemNumberTextDte(null, parser.getValueAsString());
        }
        throw JsonMappingException.from(parser, "Expected a number or string item field");
    }
}
