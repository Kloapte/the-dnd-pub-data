package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Set;

public final class BackgroundEquipmentEntryDeserializer extends StdDeserializer<BackgroundEquipmentEntryDte> {
    public BackgroundEquipmentEntryDeserializer() {
        super(BackgroundEquipmentEntryDte.class);
    }

    @Override
    public BackgroundEquipmentEntryDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new BackgroundEquipmentEntryDte(node.asText(), null, null, null, null, null, null, null);
        }
        if (!node.isObject()) {
            throw context.weirdStringException(node.toString(), BackgroundEquipmentEntryDte.class,
                    "Expected an item UID or equipment object");
        }
        node.fieldNames().forEachRemaining(field -> {
            if (!Set.of("item", "special", "equipmentType", "value", "displayName", "quantity",
                    "containsValue", "worthValue").contains(field)) {
                throw new IllegalArgumentException("Unsupported background equipment field: " + field);
            }
        });
        for (String field : Set.of("item", "special", "equipmentType", "displayName")) {
            JsonNode value = node.get(field);
            if (value != null && !value.isTextual()) {
                throw context.weirdStringException(value.toString(), BackgroundEquipmentEntryDte.class,
                        field + " must be a string");
            }
        }
        for (String field : Set.of("value", "quantity", "containsValue", "worthValue")) {
            JsonNode value = node.get(field);
            if (value != null && !value.isIntegralNumber()) {
                throw context.weirdStringException(value.toString(), BackgroundEquipmentEntryDte.class,
                        field + " must be an integer");
            }
        }
        return new BackgroundEquipmentEntryDte(
                text(node, "item"), text(node, "special"), text(node, "equipmentType"),
                integer(node, "value"), text(node, "displayName"), integer(node, "quantity"),
                integer(node, "containsValue"), integer(node, "worthValue"));
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || !value.isTextual() ? null : value.asText();
    }

    private static Integer integer(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || !value.isIntegralNumber() ? null : value.intValue();
    }
}
