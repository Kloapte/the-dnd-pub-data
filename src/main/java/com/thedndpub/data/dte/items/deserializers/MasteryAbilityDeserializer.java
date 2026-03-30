package com.thedndpub.data.dte.items.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.generic.AbilityDte;
import com.thedndpub.data.dte.generic.ChooseDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MasteryAbilityDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

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