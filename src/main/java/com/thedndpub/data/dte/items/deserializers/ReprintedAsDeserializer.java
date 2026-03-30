package com.thedndpub.data.dte.items.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class ReprintedAsDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if(node != null && node.isArray()) {
            List<String> items = new ArrayList<>();

            for (JsonNode item : node) {
                if(item.isTextual()) {
                    items.add(item.asText());
                }
                else if(item.isObject()) {
                    items.add(item.get("uid").asText());
                }
                else {
                    throw new UnexpectedException("Not expected type of reprintedAs item");
                }
            }

            return items;
        }
        else {
            throw new UnexpectedException("Not expected type of reprintedAs");
        }
    }
}
