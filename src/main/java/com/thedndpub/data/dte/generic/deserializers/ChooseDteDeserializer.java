package com.thedndpub.data.dte.generic.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.generic.ChooseDte;

import java.io.IOException;

public class ChooseDteDeserializer extends JsonDeserializer<ChooseDte> {
    @Override
    public ChooseDte deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if (node.isArray()) {
            if (node.size() == 1) {
                return mapper.convertValue(node.get(0), ChooseDte.class);
            }
            else {
                throw new RuntimeException("Multiple choice lists?");
            }
        }
        else {
            return mapper.convertValue(node, ChooseDte.class);
        }
    }
}