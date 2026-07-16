package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class SrdDeserializer extends ValueDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node != null && node.isTextual()) {
            return Boolean.TRUE;
        }
        else if(node != null && node.isBoolean()) {
            return node.asBoolean();
        }
        else {
            throw context.weirdStringException(
                    node.toString(),
                    String.class,
                    "Not expected type of Srd"
            );
//            throw new UnexpectedException("Not expected type of Srd");
        }
    }
}
