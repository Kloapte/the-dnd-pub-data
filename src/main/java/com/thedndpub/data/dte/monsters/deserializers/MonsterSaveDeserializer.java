package com.thedndpub.data.dte.monsters.deserializers;

import com.thedndpub.data.dte.monsters.AcDte;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class MonsterSaveDeserializer extends ValueDeserializer<AcDte> {
    @Override
    public AcDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        return mapAc(node, context);
    }

    private AcDte mapAc(JsonNode node, DeserializationContext context) {
        if(node.isObject()) {
            return context.readTreeAsValue(node, AcDte.class);
        }
        if(node.isNumber()) {
            return new AcDte(node.asInt(), null, null, null, null);
        }
        else {
            throw context.weirdStringException(
                    node.toString(),
                    AcDte.class,
                    "Unexpected armour class"
            );
        }
    }
}