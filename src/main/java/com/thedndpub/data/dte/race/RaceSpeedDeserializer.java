package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Set;

public final class RaceSpeedDeserializer extends StdDeserializer<RaceSpeedDte> {
    public RaceSpeedDeserializer() {
        super(RaceSpeedDte.class);
    }

    @Override
    public RaceSpeedDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new RaceSpeedDte(RaceSpeedValueDte.feet(node.intValue()), null, null, null, null);
        }
        if (node.isObject()) {
            var fields = node.fieldNames();
            while (fields.hasNext()) {
                String field = fields.next();
                if (!Set.of("walk", "fly", "swim", "climb", "burrow").contains(field)) {
                    throw new IllegalArgumentException("Unsupported race speed field: " + field);
                }
            }
            return new RaceSpeedDte(value(node, "walk"), value(node, "fly"), value(node, "swim"),
                    value(node, "climb"), value(node, "burrow"));
        }
        return (RaceSpeedDte) context.handleUnexpectedToken(RaceSpeedDte.class, parser);
    }

    private static RaceSpeedValueDte value(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isIntegralNumber()) {
            return RaceSpeedValueDte.feet(value.intValue());
        }
        if (value.isBoolean()) {
            return RaceSpeedValueDte.flag(value.booleanValue());
        }
        throw new IllegalArgumentException(
                "Race speed field '" + field + "' must be an integer or boolean");
    }
}
