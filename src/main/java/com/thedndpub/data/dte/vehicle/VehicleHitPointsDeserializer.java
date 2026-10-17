package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class VehicleHitPointsDeserializer extends JsonDeserializer<VehicleHitPointsDte> {
    @Override
    public VehicleHitPointsDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new VehicleNumericHitPointsDte(node.intValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "vehicle hp must be an integer or an object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!Set.of("average", "formula", "hp", "dt", "mt").contains(name)) {
                throw JsonMappingException.from(parser, "unexpected vehicle hp property: " + name);
            }
        }
        for (String field : Set.of("average", "hp", "dt", "mt")) {
            if (node.has(field) && !node.get(field).isIntegralNumber()) {
                throw JsonMappingException.from(parser, "vehicle hp " + field + " must be an integer");
            }
        }
        if (node.has("formula") && !node.get("formula").isTextual()) {
            throw JsonMappingException.from(parser, "vehicle hp formula must be a string");
        }
        return new VehicleDetailedHitPointsDte(integer(node, "average"), text(node, "formula"), integer(node, "hp"),
                integer(node, "dt"), integer(node, "mt"));
    }

    private static Integer integer(JsonNode node, String field) {
        return node.has(field) && node.get(field).isIntegralNumber() ? node.get(field).intValue() : null;
    }

    private static String text(JsonNode node, String field) {
        return node.has(field) && node.get(field).isTextual() ? node.get(field).textValue() : null;
    }
}
