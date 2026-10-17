package com.thedndpub.data.dte.object;

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

public final class ObjectActionDeserializer extends JsonDeserializer<ObjectActionDte> {
    @Override
    public ObjectActionDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        fields(node, Set.of("type", "name", "entries"), parser);
        String actionType = requiredText(node, "type", parser);
        if (!Set.of("actions", "entries").contains(actionType)) {
            throw JsonMappingException.from(parser, "Unsupported object action type: " + actionType);
        }
        JsonNode entriesNode = node.get("entries");
        if (entriesNode == null || !entriesNode.isArray()) {
            throw JsonMappingException.from(parser, "Object action entries must be an array");
        }
        List<ObjectActionContentDte> entries = new ArrayList<>();
        for (JsonNode entry : entriesNode) {
            if (entry.isTextual()) {
                entries.add(new ObjectActionTextDte(entry.textValue()));
            } else if (entry.isObject() && "attack".equals(text(entry, "type"))) {
                fields(entry, Set.of("type", "attackType", "attackEntries", "hitEntries"), parser);
                entries.add(new ObjectAttackDte("attack", requiredText(entry, "attackType", parser),
                        strings(entry.get("attackEntries"), parser), strings(entry.get("hitEntries"), parser)));
            } else if (entry.isObject() && "list".equals(text(entry, "type"))) {
                fields(entry, Set.of("type", "page", "style", "items"), parser);
                JsonNode itemsNode = entry.get("items");
                if (itemsNode == null || !itemsNode.isArray()) {
                    throw JsonMappingException.from(parser, "Object action list items must be an array");
                }
                List<ObjectActionItemDte> items = new ArrayList<>();
                for (JsonNode item : itemsNode) {
                    fields(item, Set.of("type", "name", "entries"), parser);
                    if (!"item".equals(requiredText(item, "type", parser))) {
                        throw JsonMappingException.from(parser, "Object action list child must have type 'item'");
                    }
                    List<EntryDte> itemEntries = new ArrayList<>();
                    JsonNode itemEntriesNode = item.get("entries");
                    if (itemEntriesNode == null || !itemEntriesNode.isArray()) {
                        throw JsonMappingException.from(parser, "Object action item entries must be an array");
                    }
                    for (JsonNode itemEntry : itemEntriesNode) {
                        itemEntries.add(parser.getCodec().treeToValue(itemEntry, EntryDte.class));
                    }
                    items.add(new ObjectActionItemDte(text(item, "type"), text(item, "name"), itemEntries));
                }
                entries.add(new ObjectActionListDte("list", integer(entry, "page"), text(entry, "style"), items));
            } else {
                throw JsonMappingException.from(parser, "Unsupported object action entry structure");
            }
        }
        return new ObjectActionDte(actionType, requiredText(node, "name", parser), entries);
    }

    private static void fields(JsonNode node, Set<String> allowed, JsonParser parser) throws IOException {
        if (node == null || !node.isObject()) {
            throw JsonMappingException.from(parser, "Object action value must be an object");
        }
        Iterator<String> names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!allowed.contains(name)) {
                throw JsonMappingException.from(parser, "Unexpected object action field: " + name);
            }
        }
    }

    private static String text(JsonNode node, String name) {
        return node.has(name) && node.get(name).isTextual() ? node.get(name).textValue() : null;
    }

    private static String requiredText(JsonNode node, String name, JsonParser parser) throws IOException {
        String value = text(node, name);
        if (value == null || value.isBlank()) {
            throw JsonMappingException.from(parser, "Object action field '" + name + "' must be non-blank text");
        }
        return value;
    }

    private static Integer integer(JsonNode node, String name) {
        return node.has(name) && node.get(name).isIntegralNumber() ? node.get(name).intValue() : null;
    }

    private static List<String> strings(JsonNode node, JsonParser parser) throws IOException {
        if (node == null || !node.isArray()) {
            throw JsonMappingException.from(parser, "Object attack entries must be arrays");
        }
        List<String> values = new ArrayList<>();
        for (JsonNode value : node) {
            if (!value.isTextual()) {
                throw JsonMappingException.from(parser, "Object attack entry must be text");
            }
            values.add(value.textValue());
        }
        return List.copyOf(values);
    }
}
