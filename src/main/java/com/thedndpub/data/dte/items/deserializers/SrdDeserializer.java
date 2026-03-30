package com.thedndpub.data.dte.items.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.rmi.UnexpectedException;

public class SrdDeserializer extends JsonDeserializer<Boolean> {
    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode node = mapper.readTree(p);

        if(node != null && node.isTextual()) {
            return Boolean.TRUE;
        }
        else if(node != null && node.isBoolean()) {
            return node.asBoolean();
        }
        else {
            throw new UnexpectedException("Not expected type of Srd");
        }
    }
}
