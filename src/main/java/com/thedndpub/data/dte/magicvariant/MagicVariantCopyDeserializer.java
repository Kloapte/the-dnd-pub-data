package com.thedndpub.data.dte.magicvariant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class MagicVariantCopyDeserializer extends JsonDeserializer<MagicVariantCopyDte> {
    private static final Set<String> COPY_FIELDS = Set.of("name", "source", "_mod", "_preserve");
    private static final Set<String> MUTABLE_TARGETS = Set.of(
            "inherits.basicRules2024",
            "inherits.classFeatures",
            "inherits.entries",
            "inherits.lootTables",
            "inherits.namePrefix",
            "inherits.nameSuffix",
            "inherits.page",
            "inherits.propertyAdd",
            "inherits.rarity",
            "inherits.resist",
            "inherits.source",
            "inherits.srd52");

    @Override
    public MagicVariantCopyDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode root = parser.readValueAsTree();
        requireObject(parser, root, "Magic variant _copy must be an object");
        checkFields(parser, root, COPY_FIELDS, "_copy");

        String name = requiredText(parser, root, "name");
        String source = requiredText(parser, root, "source");
        List<MagicVariantCopyOperationDte> operations = parseModifications(parser, root.get("_mod"));
        Boolean preservePage = parsePreserve(parser, root.get("_preserve"));
        return new MagicVariantCopyDte(name, source, preservePage, operations);
    }

    private static List<MagicVariantCopyOperationDte> parseModifications(JsonParser parser, JsonNode modifications)
            throws IOException {
        if (modifications == null) {
            return List.of();
        }
        requireObject(parser, modifications, "Magic variant _copy._mod must be an object");
        List<MagicVariantCopyOperationDte> operations = new ArrayList<>();
        Iterator<String> targets = modifications.fieldNames();
        while (targets.hasNext()) {
            String targetPath = targets.next();
            if (!MUTABLE_TARGETS.contains(targetPath)) {
                throw mappingError(parser, "Unsupported magic variant copy target '" + targetPath + "'");
            }
            operations.add(parseOperation(parser, targetPath, modifications.get(targetPath)));
        }
        return List.copyOf(operations);
    }

    private static MagicVariantCopyOperationDte parseOperation(
            JsonParser parser,
            String targetPath,
            JsonNode payload) throws IOException {
        if (payload != null && payload.isTextual() && "remove".equals(payload.textValue())) {
            if (!Set.of("inherits.basicRules2024", "inherits.classFeatures", "inherits.lootTables",
                    "inherits.srd52").contains(targetPath)) {
                throw mappingError(parser, "Remove is unsupported for target '" + targetPath + "'");
            }
            return new MagicVariantCopyOperationDte(
                    targetPath, MagicVariantCopyOperationKind.REMOVE_PROPERTY, null, null, null);
        }

        requireObject(parser, payload, "Copy operation for '" + targetPath + "' must be an object");
        JsonNode modeNode = payload.get("mode");
        if (modeNode == null || !modeNode.isTextual()) {
            throw mappingError(parser, "Copy operation for '" + targetPath + "' requires a string mode");
        }
        String mode = modeNode.textValue();
        return switch (mode) {
            case "setProp" -> parseSetProperty(parser, targetPath, payload);
            case "appendArr" -> parseAppendEntry(parser, targetPath, payload);
            case "appendIfNotExistsArr" -> parseAppendString(parser, targetPath, payload);
            default -> throw mappingError(parser, "Unsupported magic variant copy operation '" + mode + "'");
        };
    }

    private static MagicVariantCopyOperationDte parseSetProperty(
            JsonParser parser,
            String targetPath,
            JsonNode payload) throws IOException {
        checkFields(parser, payload, Set.of("mode", "value"), "setProp operation");
        JsonNode value = payload.get("value");
        if ("inherits.page".equals(targetPath)) {
            if (value == null || !value.isIntegralNumber() || !value.canConvertToInt()) {
                throw mappingError(parser, "setProp for 'inherits.page' requires an integer value");
            }
            return new MagicVariantCopyOperationDte(targetPath, MagicVariantCopyOperationKind.SET_PROPERTY,
                    null, value.intValue(), null);
        }
        if (value == null || !value.isTextual()) {
            throw mappingError(parser, "setProp for '" + targetPath + "' requires a string value");
        }
        return new MagicVariantCopyOperationDte(targetPath, MagicVariantCopyOperationKind.SET_PROPERTY,
                value.textValue(), null, null);
    }

    private static MagicVariantCopyOperationDte parseAppendEntry(
            JsonParser parser,
            String targetPath,
            JsonNode payload) throws IOException {
        checkFields(parser, payload, Set.of("mode", "items"), "appendArr operation");
        if (!"inherits.entries".equals(targetPath)) {
            throw mappingError(parser, "appendArr is unsupported for target '" + targetPath + "'");
        }
        JsonNode item = payload.get("items");
        if (item == null || !item.isObject()) {
            throw mappingError(parser, "appendArr for 'inherits.entries' requires one entry object");
        }
        EntryDte entry = parser.getCodec().treeToValue(item, EntryDte.class);
        return new MagicVariantCopyOperationDte(
                targetPath, MagicVariantCopyOperationKind.APPEND_ENTRY, null, null, entry);
    }

    private static MagicVariantCopyOperationDte parseAppendString(
            JsonParser parser,
            String targetPath,
            JsonNode payload) throws IOException {
        checkFields(parser, payload, Set.of("mode", "items"), "appendIfNotExistsArr operation");
        if (!Set.of("inherits.propertyAdd", "inherits.resist").contains(targetPath)) {
            throw mappingError(parser, "appendIfNotExistsArr is unsupported for target '" + targetPath + "'");
        }
        JsonNode item = payload.get("items");
        if (item == null || !item.isTextual()) {
            throw mappingError(parser, "appendIfNotExistsArr for '" + targetPath + "' requires a string item");
        }
        return new MagicVariantCopyOperationDte(
                targetPath, MagicVariantCopyOperationKind.APPEND_IF_ABSENT_STRING,
                item.textValue(), null, null);
    }

    private static Boolean parsePreserve(JsonParser parser, JsonNode preserve) throws IOException {
        if (preserve == null) {
            return null;
        }
        requireObject(parser, preserve, "Magic variant _copy._preserve must be an object");
        checkFields(parser, preserve, Set.of("page"), "_copy._preserve");
        JsonNode page = preserve.get("page");
        if (page == null || !page.isBoolean()) {
            throw mappingError(parser, "_copy._preserve.page must be a boolean");
        }
        return page.booleanValue();
    }

    private static String requiredText(JsonParser parser, JsonNode root, String field) throws IOException {
        JsonNode value = root.get(field);
        if (value == null || !value.isTextual() || value.textValue().isBlank()) {
            throw mappingError(parser, "Magic variant _copy requires a non-empty '" + field + "'");
        }
        return value.textValue();
    }

    private static void requireObject(JsonParser parser, JsonNode node, String message) throws IOException {
        if (node == null || !node.isObject()) {
            throw mappingError(parser, message);
        }
    }

    private static void checkFields(JsonParser parser, JsonNode node, Set<String> allowed, String context)
            throws IOException {
        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!allowed.contains(field)) {
                throw mappingError(parser, "Unknown field '" + field + "' in " + context);
            }
        }
    }

    private static IOException mappingError(JsonParser parser, String message) {
        return com.fasterxml.jackson.databind.JsonMappingException.from(parser, message);
    }
}
