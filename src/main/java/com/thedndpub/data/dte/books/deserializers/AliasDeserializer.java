package com.thedndpub.data.dte.books.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class AliasDeserializer extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if (node.isArray() && node.size() == 1) {
            return node.get(0).asText();
        }

        throw context.weirdStringException(
                node.toString(),
                String.class,
                "Alias must be an array containing exactly one value"
        );
    }
}