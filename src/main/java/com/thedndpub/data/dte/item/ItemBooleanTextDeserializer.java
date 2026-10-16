package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class ItemBooleanTextDeserializer extends JsonDeserializer<ItemBooleanTextDte> {
    @Override
    public ItemBooleanTextDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isBoolean()) {
            return new ItemBooleanTextDte(parser.getBooleanValue(), null);
        }
        if (parser.currentToken().isScalarValue()) {
            return new ItemBooleanTextDte(null, parser.getValueAsString());
        }
        throw JsonMappingException.from(parser, "Expected a boolean or string item field");
    }
}
