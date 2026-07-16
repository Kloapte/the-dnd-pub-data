package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public class ItemFocusDeserializer extends ValueDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if (node.isArray()) {
            List<String> focus = new ArrayList<>();
            for(JsonNode subItem : node) {
                focus.add(subItem.asText());
            }
            return focus;
        }
        else if(node.isBoolean()) {
            if(node.asBoolean()) {
                return List.of("All");
            }
            else return null;
        }
        else {
            throw new RuntimeException("Focus not a list");
        }
    }
}