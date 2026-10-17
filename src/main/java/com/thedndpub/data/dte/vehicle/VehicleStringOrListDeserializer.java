package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class VehicleStringOrListDeserializer extends JsonDeserializer<VehicleStringOrListDte> {
    @Override
    public VehicleStringOrListDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new VehicleStringDte(node.textValue());
        }
        if (!node.isArray()) {
            throw JsonMappingException.from(parser, "value must be a string or string array");
        }
        List<String> values = new ArrayList<>();
        for (JsonNode value : node) {
            if (!value.isTextual()) {
                throw JsonMappingException.from(parser, "string array values must be strings");
            }
            values.add(value.textValue());
        }
        return new VehicleStringListDte(values);
    }
}
