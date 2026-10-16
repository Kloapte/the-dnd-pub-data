package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class ItemNullableIntegerDeserializer extends JsonDeserializer<ItemNullableIntegerDte> {
    @Override
    public ItemNullableIntegerDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (!parser.currentToken().isNumeric()) {
            throw JsonMappingException.from(parser, "Expected an integer or null item value");
        }
        return new ItemNullableIntegerDte(parser.getIntValue(), true);
    }

    @Override
    public ItemNullableIntegerDte getNullValue(DeserializationContext context) {
        return new ItemNullableIntegerDte(null, true);
    }
}
