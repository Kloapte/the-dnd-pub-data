package com.thedndpub.data.dte.monsters.deserializers;

import com.thedndpub.data.dte.monsters.CombatRatingDte;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class WillDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isString()) {
            return node.asString();
        }
        else if(node.has("entry")) {
            return node.get("entry") + " (hidden)";
        }
        else {
            throw context.weirdStringException(
                    node.toString(),
                    String.class,
                    "Unexpected spellcasting will"
            );
        }
    }
}