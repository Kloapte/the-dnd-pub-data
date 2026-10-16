package com.thedndpub.data.dte.action;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public final class ActionTimeDeserializer extends JsonDeserializer<ActionTimeDte> {
    @Override
    public ActionTimeDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ActionTimeSpecialDte(node.textValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Action time must be a string or an object");
        }
        requireOnlyFields(node, Set.of("number", "unit"), parser);
        JsonNode number = node.get("number");
        JsonNode unit = node.get("unit");
        if (number == null || !number.isIntegralNumber()) {
            throw JsonMappingException.from(parser, "Action time number must be an integer");
        }
        if (unit == null || !unit.isTextual()) {
            throw JsonMappingException.from(parser, "Action time unit must be a string");
        }
        return new ActionTimeUnitDte(number.intValue(), unit.textValue());
    }

    private static void requireOnlyFields(JsonNode node, Set<String> allowed, JsonParser parser)
            throws JsonMappingException {
        Iterator<String> names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!allowed.contains(name)) {
                throw JsonMappingException.from(parser, "Unknown action time property '" + name + "'");
            }
        }
    }
}
