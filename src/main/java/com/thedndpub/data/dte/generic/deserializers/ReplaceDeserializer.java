package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class ReplaceDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isTextual()) {
            return node.asText();
        }
        return "";
    }
}
