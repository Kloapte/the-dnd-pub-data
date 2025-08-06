package com.thedndpub.data.dte.race.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.race.LevelSpellKnownDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class SpellKnownDteDeserializer extends JsonDeserializer<LevelSpellKnownDte> {
    @Override
    public LevelSpellKnownDte deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        LevelSpellKnownDte dte = new LevelSpellKnownDte();

        if(node != null && node.isObject()) {
            if(node.get("_") != null) {
                if(node.get("_").isArray()) {
                    dte.setSpells(new ArrayList<>());
                    for (Iterator<JsonNode> it = node.get("_").elements(); it.hasNext(); ) {
                        JsonNode stringNode = it.next();
                        if(stringNode.isObject()) {
                            dte.setChoose(stringNode.get("choose").asText());
                            if(stringNode.get("count") != null) {
                                dte.setCount(stringNode.get("count").asInt());
                            }
                        }
                        else {
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
            for (Iterator<JsonNode> it = node.elements(); it.hasNext(); ) {
                JsonNode stringNode = it.next();
                if(stringNode.isObject()) {
                    dte.setChoose(stringNode.get("choose").asText());
                    dte.setCount(stringNode.get("count").asInt());
                }
                else if(stringNode.isTextual()) {
                    dte.getSpells().add(stringNode.asText());
                }
                else {
                    throw new RuntimeException("Unsupported spell text");
                }
            }
        }

        return dte;
    }
}
