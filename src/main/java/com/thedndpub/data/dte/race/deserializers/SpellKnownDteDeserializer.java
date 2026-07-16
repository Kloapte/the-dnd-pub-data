package com.thedndpub.data.dte.race.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;
import com.thedndpub.data.dte.race.LevelSpellKnownDte;

import java.util.ArrayList;
import java.util.Iterator;

public class SpellKnownDteDeserializer extends ValueDeserializer<LevelSpellKnownDte> {
    @Override
    public LevelSpellKnownDte deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        LevelSpellKnownDte dte = new LevelSpellKnownDte();

        if(node != null && node.isObject()) {
            if(node.get("_") != null) {
                if(node.get("_").isArray()) {
                    dte.setSpells(new ArrayList<>());

                    for (JsonNode stringNode : node.get("_")) {
                        if (stringNode.isObject()) {
                            dte.setChoose(stringNode.get("choose").asText());

                            JsonNode countNode = stringNode.get("count");
                            if (countNode != null) {
                                dte.setCount(countNode.asInt());
                            }
                        } else {
                            dte.getSpells().add(stringNode.asText());
                        }
                    }
                }
                else {
                    throw new RuntimeException("Unsupported spell _");
                }
            }
            else if(node.get("rest") != null) {
                dte.setSpells(new ArrayList<>());
                dte.setRest(true);
                for(JsonNode restNode : node.get("rest").get("1")) {
                    dte.getSpells().add(restNode.asText());
                    dte.setLevel(1);
                }
            }
            else if(node.get("daily") != null) {
                dte.setSpells(new ArrayList<>());
                dte.setDaily(true);
                if(node.get("daily").get("1") != null) {
                    for (JsonNode restNode : node.get("daily").get("1")) {
                        dte.getSpells().add(restNode.asText());
                        dte.setLevel(1);
                    }
                }
                else if(node.get("daily").get("pb") != null) {
                    for (JsonNode restNode : node.get("daily").get("pb")) {
                        dte.getSpells().add(restNode.asText());
                        dte.setLevel(1);
                    }
                }
                else {
                    throw new RuntimeException("Unsupported spell daily");
                }
            }
            else {
                throw new RuntimeException("Unsupported spell");
            }
        }
        else if(node != null && node.isArray()) {
            dte.setSpells(new ArrayList<>());

            for (JsonNode stringNode : node) {
                if (stringNode.isObject()) {
                    dte.setChoose(stringNode.path("choose").asText());

                    if (stringNode.has("count")) {
                        dte.setCount(stringNode.path("count").asInt());
                    }
                } else if (stringNode.isTextual()) {
                    dte.getSpells().add(stringNode.asText());
                } else {
                    throw new RuntimeException("Unsupported spell text");
                }
            }
        }

        return dte;
    }
}
