package com.thedndpub.data.dte.items.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.util.ArrayList;
import java.util.List;

public class MasteryAbilityDeserializer extends ValueDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isArray()) {
            List<String> masteries = new ArrayList<>();

            for(JsonNode subNode : node) {
                if(subNode.isTextual()) {
                    masteries.add(subNode.asText());
                }
                else if(subNode.isObject()) {
                    masteries.add(subNode.get("uid").asText() + " - " + subNode.get("note").asText());
                }
            }
            return masteries;
        }
        else {
            throw new RuntimeException("Not a list");
        }
    }
}