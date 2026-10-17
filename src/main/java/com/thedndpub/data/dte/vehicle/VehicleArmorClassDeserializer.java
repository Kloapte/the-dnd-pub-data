package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class VehicleArmorClassDeserializer extends JsonDeserializer<VehicleArmorClassDte> {
    @Override
    public VehicleArmorClassDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new VehicleNumericArmorClassDte(node.intValue());
        }
        if (!node.isArray()) {
            throw JsonMappingException.from(parser, "vehicle ac must be an integer or an array");
        }
        List<VehicleArmorClassComponentDte> values = new ArrayList<>();
        for (JsonNode value : node) {
            requireFields(value, Set.of("ac", "from"), parser);
            if (!value.has("ac") || !value.get("ac").isIntegralNumber()) {
                throw JsonMappingException.from(parser, "vehicle ac component requires an integer ac");
            }
            List<String> from = null;
            if (value.has("from")) {
                if (!value.get("from").isArray()) {
                    throw JsonMappingException.from(parser, "vehicle ac from must be an array");
                }
                from = new ArrayList<>();
                for (JsonNode token : value.get("from")) {
                    if (!token.isTextual()) {
                        throw JsonMappingException.from(parser, "vehicle ac from values must be strings");
                    }
                    from.add(token.textValue());
                }
            }
            values.add(new VehicleArmorClassComponentDte(value.get("ac").intValue(), from));
        }
        return new VehicleDetailedArmorClassDte(values);
    }

    private static void requireFields(JsonNode node, Set<String> fields, JsonParser parser) throws IOException {
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "vehicle ac array values must be objects");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!fields.contains(name)) {
                throw JsonMappingException.from(parser, "unexpected vehicle ac property: " + name);
            }
        }
    }
}
