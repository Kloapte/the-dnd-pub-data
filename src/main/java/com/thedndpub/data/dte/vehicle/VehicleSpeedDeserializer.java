package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class VehicleSpeedDeserializer extends JsonDeserializer<VehicleSpeedDte> {
    @Override
    public VehicleSpeedDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new VehicleNumericSpeedDte(node.intValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "vehicle speed must be an integer or an object");
        }
        requireFields(node, Set.of("fly", "swim", "walk", "canHover"), parser);
        return new VehicleDetailedSpeedDte(mode(node.get("fly"), parser), mode(node.get("swim"), parser),
                integer(node.get("walk"), parser), bool(node.get("canHover"), parser));
    }

    private static VehicleSpeedModeDte mode(JsonNode node, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isIntegralNumber()) {
            return new VehicleNumericSpeedModeDte(node.intValue());
        }
        if (node.isObject()) {
            requireFields(node, Set.of("number", "condition"), parser);
            return new VehicleConditionalSpeedModeDte(integer(node.get("number"), parser),
                    text(node.get("condition"), parser));
        }
        throw JsonMappingException.from(parser, "vehicle speed mode must be an integer or object");
    }

    private static Integer integer(JsonNode node, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isIntegralNumber()) {
            throw JsonMappingException.from(parser, "speed values must be integers");
        }
        return node.intValue();
    }

    private static Boolean bool(JsonNode node, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isBoolean()) {
            throw JsonMappingException.from(parser, "canHover must be a boolean");
        }
        return node.booleanValue();
    }

    private static String text(JsonNode node, JsonParser parser) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isTextual()) {
            throw JsonMappingException.from(parser, "speed condition must be a string");
        }
        return node.textValue();
    }

    private static void requireFields(JsonNode node, Set<String> fields, JsonParser parser) throws IOException {
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!fields.contains(name)) {
                throw JsonMappingException.from(parser, "unexpected speed property: " + name);
            }
        }
    }
}
