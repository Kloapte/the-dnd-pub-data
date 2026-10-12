package com.thedndpub.data.dte.monsters.deserializers;

import com.thedndpub.data.dte.monsters.OneOfDte;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

public class SkillOtherDeserializer extends ValueDeserializer<OneOfDte> {
    @Override
    public OneOfDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);
        return map(node, context);
    }

    private OneOfDte map(JsonNode node, DeserializationContext context) {
        if (node.has(1)) {
            throw context.weirdStringException(
                    node.toString(),
                    OneOfDte.class,
                    "Has more then one item in list");
        }

        return context.readTreeAsValue(node.get(0).get(0), OneOfDte.class);
    }
}