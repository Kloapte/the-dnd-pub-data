package com.thedndpub.data.dte.item;

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

public final class ItemAttachedSpellsDeserializer extends JsonDeserializer<ItemAttachedSpellsDte> {
    private static final List<String> SCHEDULES = List.of("will", "daily", "rest", "charges", "limited");

    @Override
    public ItemAttachedSpellsDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.readValueAsTree();
        if (node.isArray()) {
            return new ItemAttachedSpellsDte(strings(parser, node), null, null, null, null,
                    null, null, List.of(), List.of());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Expected attached spells as an array or schedule object");
        }
        List<String> known = new ArrayList<>(SCHEDULES);
        known.addAll(List.of("ability", "other", "ritual"));
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!known.contains(field)) {
                throw JsonMappingException.from(parser, "Unsupported attached spell schedule: " + field);
            }
        }
        return new ItemAttachedSpellsDte(List.of(), text(parser, node.get("ability")),
                schedule(parser, node.get("will")), schedule(parser, node.get("daily")),
                schedule(parser, node.get("rest")), schedule(parser, node.get("charges")),
                schedule(parser, node.get("limited")), strings(parser, node.get("other")),
                strings(parser, node.get("ritual")));
    }

    private static ItemSpellGrantDte schedule(JsonParser parser, JsonNode node) throws IOException {
        if (node == null) {
            return null;
        }
        if (node.isArray()) {
            return new ItemSpellGrantDte(strings(parser, node), Map.of());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Expected schedule values keyed by use count");
        }
        Map<String, List<String>> values = new LinkedHashMap<>();
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            values.put(field.getKey(), strings(parser, field.getValue()));
        }
        return new ItemSpellGrantDte(List.of(), Map.copyOf(values));
    }

    private static List<String> strings(JsonParser parser, JsonNode node) throws IOException {
        if (node == null) {
            return List.of();
        }
        if (!node.isArray()) {
            throw JsonMappingException.from(parser, "Expected a list of attached spell identifiers");
        }
        List<String> values = new ArrayList<>();
        for (JsonNode value : node) {
            if (!value.isTextual()) {
                throw JsonMappingException.from(parser, "Expected attached spell identifiers to be strings");
            }
            values.add(value.textValue());
        }
        return List.copyOf(values);
    }

    private static String text(JsonParser parser, JsonNode node) throws IOException {
        if (node == null) {
            return null;
        }
        if (!node.isTextual()) {
            throw JsonMappingException.from(parser, "Expected a string spellcasting ability");
        }
        return node.textValue();
    }
}
