package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ItemStringListDeserializer extends JsonDeserializer<ItemStringListDte> {
    @Override
    public ItemStringListDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isBoolean()) {
            return new ItemStringListDte(List.of(), parser.getBooleanValue());
        }
        if (!parser.currentToken().isStructStart()) {
            throw JsonMappingException.from(parser, "Expected a string array or boolean item field");
        }
        List<String> values = new ArrayList<>();
        while (parser.nextToken().isScalarValue()) {
            if (!parser.currentToken().isStructStart() && !parser.currentToken().isScalarValue()) {
                throw JsonMappingException.from(parser, "Expected string values in item field array");
            }
            if (!parser.currentToken().isScalarValue() || parser.currentToken().isBoolean()) {
                throw JsonMappingException.from(parser, "Expected string values in item field array");
            }
            values.add(parser.getValueAsString());
        }
        if (parser.currentToken() != com.fasterxml.jackson.core.JsonToken.END_ARRAY) {
            throw JsonMappingException.from(parser, "Expected a string array");
        }
        return new ItemStringListDte(List.copyOf(values), null);
    }
}
