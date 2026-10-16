package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ItemCopyArrayModificationDeserializer
        extends JsonDeserializer<ItemCopyArrayModificationDte> {
    @Override
    public ItemCopyArrayModificationDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.readValueAsTree();
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Expected an item copy array modification object");
        }
        String mode = text(parser, node, "mode", true);
        Integer index = integer(parser, node, "index");
        Integer replaceIndex = null;
        if (node.has("replace")) {
            JsonNode replace = node.get("replace");
            if (!replace.isObject()) {
                throw JsonMappingException.from(parser, "Expected replace to contain an index");
            }
            checkFields(parser, replace, List.of("index"));
            replaceIndex = integer(parser, replace, "index");
        }
        ItemCopyItemsDte items = parseItems(parser, node.get("items"));
        checkFields(parser, node, List.of("mode", "index", "replace", "items"));
        return new ItemCopyArrayModificationDte(mode, index, items, replaceIndex);
    }

    private static ItemCopyItemsDte parseItems(JsonParser parser, JsonNode node) throws IOException {
        if (node == null) {
            return null;
        }
        if (node.isTextual()) {
            return new ItemCopyItemsDte(node.textValue(), List.of());
        }
        List<com.thedndpub.data.dte.common.entry.EntryDte> entries = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode entry : node) {
                entries.add(parser.getCodec().treeToValue(entry,
                        com.thedndpub.data.dte.common.entry.EntryDte.class));
            }
        } else {
            entries.add(parser.getCodec().treeToValue(node,
                    com.thedndpub.data.dte.common.entry.EntryDte.class));
        }
        return new ItemCopyItemsDte(null, List.copyOf(entries));
    }

    private static String text(JsonParser parser, JsonNode node, String field, boolean required)
            throws IOException {
        JsonNode value = node.get(field);
        if (value == null && !required) {
            return null;
        }
        if (value == null || !value.isTextual()) {
            throw JsonMappingException.from(parser, "Expected string field '" + field + "'");
        }
        return value.textValue();
    }

    private static Integer integer(JsonParser parser, JsonNode node, String field) throws IOException {
        JsonNode value = node.get(field);
        if (value == null) {
            return null;
        }
        if (!value.isIntegralNumber()) {
            throw JsonMappingException.from(parser, "Expected integer field '" + field + "'");
        }
        return value.intValue();
    }

    private static void checkFields(JsonParser parser, JsonNode node, List<String> known) throws IOException {
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!known.contains(field)) {
                throw JsonMappingException.from(parser, "Unsupported item copy modification field: " + field);
            }
        }
    }
}
