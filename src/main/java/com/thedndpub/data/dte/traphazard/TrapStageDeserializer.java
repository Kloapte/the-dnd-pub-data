package com.thedndpub.data.dte.traphazard;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class TrapStageDeserializer extends JsonDeserializer<TrapStageDte> {
    @Override
    public TrapStageDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new TrapStageTextDte(node.textValue());
        }
        requireObject(node, parser, "stage");
        requireFields(node, Set.of("type", "style", "items"), parser, "stage");
        String type = requiredText(node, "type", parser, "stage");
        String style = optionalText(node, "style", parser, "stage");
        JsonNode itemNodes = node.get("items");
        if (itemNodes == null || !itemNodes.isArray()) {
            throw error(parser, "stage.items", "must be an array");
        }
        List<TrapStageItemDte> items = new ArrayList<>();
        int index = 0;
        for (JsonNode item : itemNodes) {
            String path = "stage.items[" + index++ + "]";
            requireObject(item, parser, path);
            requireFields(item, Set.of("type", "name", "entry", "entries"), parser, path);
            JsonNode entryNode = item.get("entry");
            JsonNode entriesNode = item.get("entries");
            if (entryNode != null && entriesNode != null) {
                throw error(parser, path, "cannot contain both entry and entries");
            }
            List<EntryDte> entries = null;
            if (entriesNode != null) {
                if (!entriesNode.isArray()) {
                    throw error(parser, path + ".entries", "must be an array");
                }
                entries = new ArrayList<>();
                for (JsonNode child : entriesNode) {
                    entries.add(parser.getCodec().treeToValue(child, EntryDte.class));
                }
            }
            items.add(new TrapStageItemDte(requiredText(item, "type", parser, path),
                    optionalText(item, "name", parser, path), optionalText(item, "entry", parser, path), entries));
        }
        return new TrapStageGroupDte(type, style, items);
    }

    private static void requireObject(JsonNode node, JsonParser parser, String path) throws IOException {
        if (!node.isObject()) {
            throw error(parser, path, "must be an object");
        }
    }

    private static void requireFields(JsonNode node, Set<String> allowed, JsonParser parser, String path)
            throws IOException {
        Iterator<String> names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!allowed.contains(name)) {
                throw error(parser, path + "." + name, "is not a supported field");
            }
        }
    }

    private static String requiredText(JsonNode node, String field, JsonParser parser, String path)
            throws IOException {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual()) {
            throw error(parser, path + "." + field, "must be a string");
        }
        return value.textValue();
    }

    private static String optionalText(JsonNode node, String field, JsonParser parser, String path)
            throws IOException {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isTextual()) {
            throw error(parser, path + "." + field, "must be a string");
        }
        return value.textValue();
    }

    private static JsonMappingException error(JsonParser parser, String path, String message) {
        return JsonMappingException.from(parser, path + " " + message);
    }
}
