package com.thedndpub.data.dte.monsters.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class AlignmentDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isString()) {
            return node.asString();
        }
        else {
            if(node.get("alignment") != null) {
                StringBuilder alignment = new StringBuilder();
                for(JsonNode subNode : node.get("alignment")) {
                    alignment.append(subNode.asString());
                }
                return alignment.toString();
            }
            else if (node.get("special") != null) {
                return node.get("special").asString();
            }
            else {
                throw context.weirdStringException(
                        node.toString(),
                        String.class,
                        "Invalid alignment"
                );
            }
        }
    }
}