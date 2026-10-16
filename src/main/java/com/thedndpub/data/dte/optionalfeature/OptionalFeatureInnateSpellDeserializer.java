package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class OptionalFeatureInnateSpellDeserializer extends StdDeserializer<OptionalFeatureInnateSpellDte> {
    public OptionalFeatureInnateSpellDeserializer() {
        super(OptionalFeatureInnateSpellDte.class);
    }

    @Override
    public OptionalFeatureInnateSpellDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "Innate spell declaration must be an object");
        }

        List<String> spells = new ArrayList<>();
        Map<String, List<String>> daily = new TreeMap<>();
        Map<String, List<String>> resource = new TreeMap<>();
        List<OptionalFeatureSpellPrerequisiteDte> ritual = new ArrayList<>();
        boolean foundValue = false;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            JsonToken valueToken = parser.nextToken();
            if (!"_".equals(field)) {
                throw JsonMappingException.from(parser, "Unsupported innate spell declaration field: " + field);
            }
            if (foundValue) {
                throw JsonMappingException.from(parser, "Duplicate innate spell declaration value");
            }
            foundValue = true;
            if (valueToken == JsonToken.START_ARRAY) {
                readSpellList(parser, spells);
            } else if (valueToken == JsonToken.START_OBJECT) {
                readUsageObject(parser, daily, resource, ritual);
            } else {
                throw JsonMappingException.from(parser, "Innate spell value must be an array or object");
            }
        }
        if (!foundValue) {
            throw JsonMappingException.from(parser, "Innate spell declaration requires an underscore value");
        }
        return new OptionalFeatureInnateSpellDte(spells, daily, resource, ritual);
    }

    private static void readSpellList(JsonParser parser, List<String> spells) throws IOException {
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() != JsonToken.VALUE_STRING) {
                throw JsonMappingException.from(parser, "Innate spell UID must be a string");
            }
            spells.add(parser.getValueAsString());
        }
    }

    private static void readUsageObject(
            JsonParser parser,
            Map<String, List<String>> daily,
            Map<String, List<String>> resource,
            List<OptionalFeatureSpellPrerequisiteDte> ritual) throws IOException {
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            JsonToken valueToken = parser.nextToken();
            switch (field) {
                case "daily" -> readSpellMap(parser, valueToken, daily);
                case "resource" -> readSpellMap(parser, valueToken, resource);
                case "ritual" -> readRituals(parser, valueToken, ritual);
                default -> throw JsonMappingException.from(parser, "Unsupported innate spell field: " + field);
            }
        }
    }

    private static void readSpellMap(
            JsonParser parser,
            JsonToken token,
            Map<String, List<String>> target) throws IOException {
        if (token != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "Innate spell usage values must be an object");
        }
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String usage = parser.currentName();
            JsonToken listToken = parser.nextToken();
            if (listToken != JsonToken.START_ARRAY) {
                throw JsonMappingException.from(parser, "Innate spell usage entry must be an array");
            }
            List<String> spellUids = new ArrayList<>();
            readSpellList(parser, spellUids);
            target.put(usage, List.copyOf(spellUids));
        }
    }

    private static void readRituals(
            JsonParser parser,
            JsonToken token,
            List<OptionalFeatureSpellPrerequisiteDte> ritual) throws IOException {
        if (token != JsonToken.START_ARRAY) {
            throw JsonMappingException.from(parser, "Innate spell ritual values must be an array");
        }
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken() == JsonToken.VALUE_STRING) {
                ritual.add(new OptionalFeatureSpellPrerequisiteDte(
                        parser.getValueAsString(), null, null, null, null));
            } else if (parser.currentToken() == JsonToken.START_OBJECT) {
                readRitualChoice(parser, ritual);
            } else {
                throw JsonMappingException.from(parser, "Invalid innate spell ritual choice");
            }
        }
    }

    private static void readRitualChoice(
            JsonParser parser,
            List<OptionalFeatureSpellPrerequisiteDte> ritual) throws IOException {
        String choose = null;
        Integer count = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            JsonToken token = parser.nextToken();
            if ("choose".equals(field) && token == JsonToken.VALUE_STRING) {
                choose = parser.getValueAsString();
            } else if ("count".equals(field) && token == JsonToken.VALUE_NUMBER_INT) {
                count = parser.getIntValue();
            } else {
                throw JsonMappingException.from(parser, "Unsupported or invalid ritual choice field: " + field);
            }
        }
        if (choose == null || count == null) {
            throw JsonMappingException.from(parser, "Ritual choice requires choose and count");
        }
        ritual.add(new OptionalFeatureSpellPrerequisiteDte(null, null, null, choose, count));
    }
}
