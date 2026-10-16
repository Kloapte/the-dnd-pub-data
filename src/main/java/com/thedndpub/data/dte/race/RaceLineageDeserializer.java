package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class RaceLineageDeserializer extends StdDeserializer<RaceLineageDte> {
    public RaceLineageDeserializer() {
        super(RaceLineageDte.class);
    }

    @Override
    public RaceLineageDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode value = parser.getCodec().readTree(parser);
        if (value.isBoolean()) {
            return new RaceLineageDte(value.booleanValue(), null);
        }
        if (value.isTextual()) {
            return new RaceLineageDte(null, value.textValue());
        }
        return (RaceLineageDte) context.handleUnexpectedToken(RaceLineageDte.class, parser);
    }
}
