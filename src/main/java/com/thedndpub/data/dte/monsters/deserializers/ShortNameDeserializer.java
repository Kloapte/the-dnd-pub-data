package com.thedndpub.data.dte.monsters.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class ShortNameDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isString()) {
            return node.asString();
        }
        else if(node.isBoolean()) {
            return node.asString(); //TODO do we want this or just ignore
        }
        else {
            throw context.weirdStringException(
                    node.toString(),
                    String.class,
                    "Unexpected Short Name type"
            );
        }
    }
}