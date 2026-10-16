package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

public final class ItemNullableStringDeserializer extends JsonDeserializer<ItemNullableStringDte> {
    @Override
    public ItemNullableStringDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (!parser.currentToken().isScalarValue() || parser.currentToken().isBoolean()) {
            throw JsonMappingException.from(parser, "Expected a string or null item value");
        }
        return new ItemNullableStringDte(parser.getValueAsString(), true);
    }

    @Override
    public ItemNullableStringDte getNullValue(DeserializationContext context) {
        return new ItemNullableStringDte(null, true);
    }
}
