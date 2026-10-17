package com.thedndpub.data.dte.vehicle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class VehicleActionDeserializer extends JsonDeserializer<VehicleActionDte> {
    @Override
    public VehicleActionDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new VehicleActionTextDte(node.textValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "vehicle action must be a string or object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String field = names.next();
            if (!Set.of("type", "style", "items", "name", "entries").contains(field)) {
                throw JsonMappingException.from(parser, "unexpected vehicle action property: " + field);
            }
        }
        List<VehicleActionItemDte> items = null;
        if (node.has("items")) {
            JsonNode sourceItems = node.get("items");
            if (!sourceItems.isArray()) {
                throw JsonMappingException.from(parser, "vehicle action items must be an array");
            }
            items = new ArrayList<>();
            for (JsonNode item : sourceItems) {
                requireFields(item, Set.of("type", "name", "entry"), parser);
                items.add(new VehicleActionItemDte(text(item, "type", parser), text(item, "name", parser),
                        text(item, "entry", parser)));
            }
        }
        List<EntryDte> entries = null;
        if (node.has("entries")) {
            JsonNode sourceEntries = node.get("entries");
            if (!sourceEntries.isArray()) {
                throw JsonMappingException.from(parser, "vehicle action entries must be an array");
            }
            entries = new ArrayList<>();
            for (JsonNode entry : sourceEntries) {
                entries.add(parser.getCodec().treeToValue(entry, EntryDte.class));
            }
        }
        return new VehicleActionObjectDte(text(node, "type", parser), text(node, "style", parser), items,
                text(node, "name", parser), entries);
    }

    private static String text(JsonNode node, String field, JsonParser parser) throws IOException {
        if (!node.has(field) || node.get(field).isNull()) {
            return null;
        }
        if (!node.get(field).isTextual()) {
            throw JsonMappingException.from(parser, "vehicle action " + field + " must be a string");
        }
        return node.get(field).textValue();
    }

    private static void requireFields(JsonNode node, Set<String> allowed, JsonParser parser) throws IOException {
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "vehicle action item must be an object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String field = names.next();
            if (!allowed.contains(field)) {
                throw JsonMappingException.from(parser, "unexpected vehicle action item property: " + field);
            }
        }
    }
}
