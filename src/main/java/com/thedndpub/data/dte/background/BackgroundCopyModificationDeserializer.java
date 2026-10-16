package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class BackgroundCopyModificationDeserializer
        extends StdDeserializer<BackgroundCopyModificationDte> {
    public BackgroundCopyModificationDeserializer() {
        super(BackgroundCopyModificationDte.class);
    }

    @Override
    public BackgroundCopyModificationDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (!node.isObject() || node.size() != 1 || !node.has("entries")) {
            throw context.weirdStringException(node.toString(), BackgroundCopyModificationDte.class,
                    "Only the observed entries copy modification is supported");
        }
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        JsonNode rawOperations = node.get("entries");
        List<BackgroundEntryModificationDte> operations = new ArrayList<>();
        if (rawOperations.isArray()) {
            for (JsonNode operation : rawOperations) {
                operations.add(readOperation(operation, mapper, context));
            }
        } else {
            operations.add(readOperation(rawOperations, mapper, context));
        }
        return new BackgroundCopyModificationDte(operations);
    }

    private static BackgroundEntryModificationDte readOperation(
            JsonNode operation,
            ObjectMapper mapper,
            DeserializationContext context) throws IOException {
        if (!operation.isObject()) {
            throw context.weirdStringException(operation.toString(), BackgroundCopyModificationDte.class,
                    "Entry modification must be an object");
        }
        operation.fieldNames().forEachRemaining(field -> {
            if (!Set.of("mode", "index", "replace", "items").contains(field)) {
                throw new IllegalArgumentException("Unsupported background entry modification field: " + field);
            }
        });
        String mode = text(operation, "mode");
        if (!"insertArr".equals(mode) && !"replaceArr".equals(mode)) {
            throw context.weirdStringException(mode, BackgroundCopyModificationDte.class,
                    "Unsupported entries copy modification mode");
        }
        JsonNode payload = operation.get("items");
        if (payload == null) {
            throw context.weirdStringException(operation.toString(), BackgroundCopyModificationDte.class,
                    "Copy modification must include items");
        }
        List<EntryDte> entries = new ArrayList<>();
        if (payload.isArray()) {
            for (JsonNode item : payload) {
                entries.add(mapper.treeToValue(item, EntryDte.class));
            }
        } else {
            entries.add(mapper.treeToValue(payload, EntryDte.class));
        }
        JsonNode replace = operation.get("replace");
        if (replace != null && replace.isObject()) {
            replace.fieldNames().forEachRemaining(field -> {
                if (!Set.of("index", "name").contains(field)) {
                    throw new IllegalArgumentException("Unsupported background replacement target field: " + field);
                }
            });
        }
        JsonNode index = "insertArr".equals(mode) ? operation.get("index")
                : replace != null && replace.isObject() ? replace.get("index") : null;
        String replaceName = replace != null && replace.isTextual()
                ? replace.asText() : replace != null && replace.isObject() ? text(replace, "name") : null;
        if ("insertArr".equals(mode) && (index == null || !index.isIntegralNumber())) {
            throw context.weirdStringException(operation.toString(), BackgroundCopyModificationDte.class,
                    "insertArr requires an integer index");
        }
        if ("replaceArr".equals(mode) && index == null && replaceName == null) {
            throw context.weirdStringException(operation.toString(), BackgroundCopyModificationDte.class,
                    "replaceArr requires a name or index target");
        }
        return new BackgroundEntryModificationDte(mode,
                index != null && index.isIntegralNumber() ? index.intValue() : null,
                replaceName, entries);
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || !value.isTextual() ? null : value.asText();
    }
}
