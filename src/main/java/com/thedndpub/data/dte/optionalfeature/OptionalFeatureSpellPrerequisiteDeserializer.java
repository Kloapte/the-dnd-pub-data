package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class OptionalFeatureSpellPrerequisiteDeserializer
        extends StdDeserializer<OptionalFeatureSpellPrerequisiteDte> {
    public OptionalFeatureSpellPrerequisiteDeserializer() {
        super(OptionalFeatureSpellPrerequisiteDte.class);
    }

    @Override
    public OptionalFeatureSpellPrerequisiteDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new OptionalFeatureSpellPrerequisiteDte(parser.getValueAsString(), null, null, null, null);
        }
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "Spell prerequisite must be a string or object");
        }

        String entry = null;
        String entrySummary = null;
        String choose = null;
        Integer count = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            JsonToken valueToken = parser.nextToken();
            switch (field) {
                case "entry" -> entry = requireString(parser, valueToken, field);
                case "entrySummary" -> entrySummary = requireString(parser, valueToken, field);
                case "choose" -> choose = requireString(parser, valueToken, field);
                case "count" -> count = requireInteger(parser, valueToken, field);
                default -> throw JsonMappingException.from(parser, "Unsupported spell prerequisite field: " + field);
            }
        }
        return new OptionalFeatureSpellPrerequisiteDte(null, entry, entrySummary, choose, count);
    }

    private static String requireString(JsonParser parser, JsonToken token, String field) throws IOException {
        if (token != JsonToken.VALUE_STRING) {
            throw JsonMappingException.from(parser, "Spell prerequisite field '" + field + "' must be a string");
        }
        return parser.getValueAsString();
    }

    private static Integer requireInteger(JsonParser parser, JsonToken token, String field) throws IOException {
        if (token != JsonToken.VALUE_NUMBER_INT) {
            throw JsonMappingException.from(parser, "Spell prerequisite field '" + field + "' must be an integer");
        }
        return parser.getIntValue();
    }
}
