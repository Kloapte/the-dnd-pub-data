package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class VehicleSpeedModeDeserializer extends JsonDeserializer<VehicleSpeedModeDte> {
    @Override
    public VehicleSpeedModeDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new VehicleNumericSpeedModeDte(node.intValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "speed mode must be an integer or object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!Set.of("number", "condition").contains(name)) {
                throw JsonMappingException.from(parser, "unexpected speed mode property: " + name);
            }
        }
        Integer number = null;
        String condition = null;
        if (node.has("number")) {
            if (!node.get("number").isIntegralNumber()) {
                throw JsonMappingException.from(parser, "speed mode number must be an integer");
            }
            number = node.get("number").intValue();
        }
        if (node.has("condition")) {
            if (!node.get("condition").isTextual()) {
                throw JsonMappingException.from(parser, "speed mode condition must be a string");
            }
            condition = node.get("condition").textValue();
        }
        return new VehicleConditionalSpeedModeDte(number, condition);
    }
}
