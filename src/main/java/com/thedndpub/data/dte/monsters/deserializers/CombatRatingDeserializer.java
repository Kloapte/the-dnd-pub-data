package com.thedndpub.data.dte.monsters.deserializers;

import com.thedndpub.data.dte.monsters.CombatRatingDte;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class CombatRatingDeserializer extends ValueDeserializer<CombatRatingDte> {
    @Override
    public CombatRatingDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        return mapCr(node, context);
    }

    private CombatRatingDte mapCr(JsonNode node, DeserializationContext context) {
        if(node.isObject()) {
            return context.readTreeAsValue(node, CombatRatingDte.class);
        }
        if(node.isString()) {
            return new CombatRatingDte(node.asString(), null, null, null, null);
        }
        else {
            throw context.weirdStringException(
                    node.toString(),
                    CombatRatingDte.class,
                    "Unexpected combat rating"
            );
        }
    }
}