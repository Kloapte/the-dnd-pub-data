package com.thedndpub.data.dte.items.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemFocusDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            List<String> focus = new ArrayList<>();
            for(JsonNode subItem : node) {
                focus.add(subItem.asText());
            }
            return focus;
        }
        else if(node.isBoolean()) {
            if(node.asBoolean()) {
                return List.of("All");
            }
            else return null;
        }
        else {
            throw new RuntimeException("Focus not a list");
        }
    }
}