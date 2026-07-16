package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public class ReprintedAsDeserializer extends ValueDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context){
        JsonNode node = context.readTree(parser);

        if(node != null && node.isArray()) {
            List<String> items = new ArrayList<>();

            for (JsonNode item : node) {
                if(item.isTextual()) {
                    items.add(item.asText());
                }
                else if(item.isObject()) {
                    items.add(item.get("uid").asText());
                }
                else {
//                    throw new UnexpectedException("Not expected type of reprintedAs item");
                    throw context.weirdStringException(
                            node.toString(),
                            String.class,
                            "Not expected type of reprintedAs item"
                    );
                }
            }

            return items;
        }
        else {
//            throw new UnexpectedException("Not expected type of reprintedAs");
            throw context.weirdStringException(
                    node.toString(),
                    String.class,
                    "Not expected type of reprintedAs"
            );
        }
    }
}
