package com.thedndpub.data.dte.books.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeaderDeserializer extends ValueDeserializer<List<String>> {

    @Override
    public List<String> deserialize(
            JsonParser parser,
            DeserializationContext context
    ) {
        JsonNode node = context.readTree(parser);

        if (!node.isArray()) {
            throw new IllegalArgumentException(
                    "Expected headers to be an array, but received: " + node
            );
        }

        List<String> headers = new ArrayList<>();

        for (JsonNode subNode : node) {
            if (subNode.isTextual()) {
                headers.add(subNode.asText());
            } else if (subNode.isObject() && subNode.hasNonNull("header")) {
                headers.add(subNode.get("header").asText());
            } else {
                throw context.weirdStringException(
                        node.toString(),
                        String.class,
                        "Expected a string or an object containing 'header'"
                );
            }
        }

        return headers;
    }
}