package com.thedndpub.data.dte.monsters.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.node.ArrayNode;

public class MonsterTypeDeserializer extends ValueDeserializer<String> {
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isString()) {
            return node.asString();
        }
        else {

            if(node.has("type") && node.get("type").isString()) {
                String type = node.get("type").asString();

                if(node.has("swarmSize")) {
                    type += "-" + node.get("swamSize");
                }

                return type;
            }
            else if(node.has("type") && node.get("type").has("choose")) {
                ArrayNode array = node.get("type").get("choose").asArray();
                StringBuilder type = new StringBuilder(array.get(0).asString() + ":");
                for(int i = 1; i < array.size(); i++) {
                    type.append(array.get(i).asString());
                    if(i < array.size() - 1) {
                        type.append(":");
                    }
                }
                return type.toString();
            }
            else {
                throw context.weirdStringException(
                        node.toString(),
                        String.class,
                        "Unexpected monster type"
                );
            }
        }
    }
}