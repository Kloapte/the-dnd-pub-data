package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class RewardAdditionalSpellDeserializer extends JsonDeserializer<RewardAdditionalSpellDte> {
    @Override
    public RewardAdditionalSpellDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        requireOnly(node, Set.of("ability", "innate", "known", "resourceName"), parser);
        RewardSpellAbilityDte ability = read(parser, node.get("ability"), RewardSpellAbilityDte.class);
        RewardSpellCollectionDte innate = readInnate(parser, node.get("innate"));
        RewardSpellListDte known = readKnown(parser, node.get("known"));
        String resourceName = readString(parser, node.get("resourceName"), "resourceName");
        return new RewardAdditionalSpellDte(ability, innate, known, resourceName);
    }

    private static RewardSpellCollectionDte readInnate(JsonParser parser, JsonNode node) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        requireOnly(node, Set.of("_"), parser);
        JsonNode payload = node.get("_");
        if (payload == null || payload.isNull()) {
            return null;
        }
        if (payload.isArray()) {
            return new RewardSpellIdsDte(readStrings(parser, payload, "innate._"));
        }
        requireOnly(payload, Set.of("daily", "limited", "rest", "ritual"), parser);
        return new RewardSpellFrequencyDte(
                readFrequency(parser, payload.get("daily"), "innate._.daily"),
                readFrequency(parser, payload.get("limited"), "innate._.limited"),
                readFrequency(parser, payload.get("rest"), "innate._.rest"),
                readStrings(parser, payload.get("ritual"), "innate._.ritual"));
    }

    private static RewardSpellListDte readKnown(JsonParser parser, JsonNode node) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        requireOnly(node, Set.of("_"), parser);
        return new RewardSpellListDte(readStrings(parser, node.get("_"), "known._"));
    }

    private static Map<String, List<String>> readFrequency(JsonParser parser, JsonNode node, String path)
            throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, path + " must be an object keyed by authored frequency");
        }
        Map<String, List<String>> result = new LinkedHashMap<>();
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            result.put(field.getKey(), readStrings(parser, field.getValue(), path + "." + field.getKey()));
        }
        return Map.copyOf(result);
    }

    private static List<String> readStrings(JsonParser parser, JsonNode node, String path) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isArray()) {
            throw JsonMappingException.from(parser, path + " must be an array of strings");
        }
        List<String> result = new ArrayList<>();
        for (JsonNode value : node) {
            if (!value.isTextual()) {
                throw JsonMappingException.from(parser, path + " must contain only strings");
            }
            result.add(value.textValue());
        }
        return List.copyOf(result);
    }

    private static String readString(JsonParser parser, JsonNode node, String path) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        if (!node.isTextual()) {
            throw JsonMappingException.from(parser, path + " must be a string");
        }
        return node.textValue();
    }

    private static <T> T read(JsonParser parser, JsonNode node, Class<T> type) throws IOException {
        return node == null || node.isNull() ? null : parser.getCodec().treeToValue(node, type);
    }

    private static void requireOnly(JsonNode node, Set<String> allowed, JsonParser parser)
            throws JsonMappingException {
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "expected object in reward spell structure");
        }
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String name = fields.next();
            if (!allowed.contains(name)) {
                throw JsonMappingException.from(parser, "unexpected reward spell property: " + name);
            }
        }
    }
}
