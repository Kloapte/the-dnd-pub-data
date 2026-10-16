package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RaceModificationDeserializer extends StdDeserializer<RaceModificationDte> {
    public RaceModificationDeserializer() {
        super(RaceModificationDte.class);
    }

    @Override
    public RaceModificationDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode root = parser.getCodec().readTree(parser);
        if (!root.isObject() || root.size() != 1 || !root.has("entries")) {
            throw new IllegalArgumentException("Race modification must contain only an entries operation set");
        }
        JsonNode operations = root.path("entries");
        if (operations.isMissingNode() || operations.isNull()) {
            return new RaceModificationDte(List.of());
        }
        List<JsonNode> operationNodes = new ArrayList<>();
        if (operations.isArray()) {
            operations.forEach(operationNodes::add);
        } else if (operations.isObject()) {
            operationNodes.add(operations);
        } else {
            return (RaceModificationDte) context.handleUnexpectedToken(
                    RaceModificationDte.class, parser);
        }

        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        List<RaceEntryModificationDte> result = new ArrayList<>();
        for (JsonNode operation : operationNodes) {
            result.add(readOperation(operation, mapper));
        }
        return new RaceModificationDte(result);
    }

    private static RaceEntryModificationDte readOperation(JsonNode operation, ObjectMapper mapper)
            throws IOException {
        var fields = operation.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!List.of("mode", "replace", "names", "with", "flags", "items").contains(field)) {
                throw new IllegalArgumentException("Unsupported race modification property: " + field);
            }
        }
        String mode = requiredText(operation, "mode");
        JsonNode itemNode = operation.get("items");
        List<EntryDte> items = new ArrayList<>();
        if (itemNode != null && !itemNode.isNull()) {
            if (itemNode.isArray()) {
                for (JsonNode item : itemNode) {
                    items.add(mapper.treeToValue(item, EntryDte.class));
                }
            } else {
                items.add(mapper.treeToValue(itemNode, EntryDte.class));
            }
        }
        List<String> names = new ArrayList<>();
        JsonNode namesNode = operation.get("names");
        if (namesNode != null && !namesNode.isNull()) {
            if (namesNode.isArray()) {
                namesNode.forEach(name -> names.add(name.asText()));
            } else {
                names.add(namesNode.asText());
            }
        }
        RaceEntryModificationDte result = new RaceEntryModificationDte(
                mode,
                text(operation, "replace"),
                names,
                text(operation, "with"),
                text(operation, "flags"),
                items);
        validateOperation(result);
        return result;
    }

    private static void validateOperation(RaceEntryModificationDte operation) {
        switch (operation.mode()) {
            case "appendArr", "prependArr" -> {
                if (operation.items().isEmpty()) {
                    throw new IllegalArgumentException(operation.mode() + " requires nonempty items");
                }
            }
            case "replaceArr" -> {
                if (operation.replace() == null || operation.items().isEmpty()) {
                    throw new IllegalArgumentException("replaceArr requires replace and nonempty items");
                }
            }
            case "removeArr" -> {
                if (operation.names().isEmpty()) {
                    throw new IllegalArgumentException("removeArr requires names");
                }
            }
            case "replaceTxt" -> {
                if (operation.replace() == null || operation.with() == null) {
                    throw new IllegalArgumentException("replaceTxt requires replace and with");
                }
                if (operation.flags() != null && !"i".equalsIgnoreCase(operation.flags())) {
                    throw new IllegalArgumentException("Unsupported replaceTxt flags: " + operation.flags());
                }
            }
            default -> throw new IllegalArgumentException("Unsupported race modification operation: "
                    + operation.mode());
        }
    }

    private static String requiredText(JsonNode node, String field) {
        String value = text(node, field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Race modification requires '" + field + "'");
        }
        return value;
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }
}
