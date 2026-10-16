package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class RaceReprintDeserializer extends StdDeserializer<RaceReprintDte> {
    public RaceReprintDeserializer() {
        super(RaceReprintDte.class);
    }

    @Override
    public RaceReprintDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new RaceReprintDte(node.asText(), null);
        }
        if (node.isObject() && node.hasNonNull("uid")) {
            return new RaceReprintDte(node.get("uid").asText(), text(node, "tag"));
        }
        return (RaceReprintDte) context.handleUnexpectedToken(RaceReprintDte.class, parser);
    }

    private static String text(JsonNode node, String name) {
        JsonNode value = node.get(name);
        return value == null || value.isNull() ? null : value.asText();
    }
}
