package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.generic.ChooseDte;

public class ChooseDteDeserializer extends ValueDeserializer<ChooseDte> {
    @Override
    public ChooseDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if (node.isArray()) {
            if (node.size() == 1) {
                return context.readTreeAsValue(node.get(0), ChooseDte.class);
            }
            else {
                throw new RuntimeException("Multiple choice lists?");
            }
        }
        else {
            return context.readTreeAsValue(node, ChooseDte.class);
        }
    }
}