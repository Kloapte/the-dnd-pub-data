package com.thedndpub.data.dte.books.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeaderDeserializer extends JsonDeserializer<List<String>> {
    @Override
    public List<String> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            List<String> headers = new ArrayList<>();
            for(JsonNode subNode : node) {
                if (subNode.isTextual()) {
                    headers.add(subNode.asText());
                } else if (subNode.isObject() && subNode.hasNonNull("header")) {
                    headers.add(subNode.get("header").asText());
                } else {
                    throw new RuntimeException("Header is weird");
                }
            }
            return headers;
        } else {
            throw new RuntimeException("Not a list");
        }
    }
}