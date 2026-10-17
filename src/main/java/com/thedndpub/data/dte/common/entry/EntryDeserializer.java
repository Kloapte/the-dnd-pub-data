package com.thedndpub.data.dte.common.entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.thedndpub.data.dte.reward.RewardInlineObjectStatblockDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Dispatches the observed prose entry forms while keeping recursive content typed.
 */
public final class EntryDeserializer extends JsonDeserializer<EntryDte> {
    @Override
    public EntryDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return read(parser.getCodec().readTree(parser), parser, "$entry");
    }

    static EntryDte read(JsonNode node, JsonParser parser, String path) throws IOException {
        if (node.isTextual()) {
            return new TextEntryDte(node.textValue());
        }
        if (!node.isObject()) {
            throw error(parser, path, "must be a string or entry object");
        }
        String type = string(node, "type", parser, path, true);
        return switch (type) {
            case "statblockInline" -> {
                fields(node, Set.of("type", "statblockType", "statblockData"), parser, path);
                if (!"object".equals(string(node, "statblockType", parser, path, true))) {
                    throw error(parser, path + ".statblockType", "must be the observed value 'object'");
                }
                JsonNode statblock = node.get("statblockData");
                if (statblock == null || !statblock.isObject()) {
                    throw error(parser, path + ".statblockData", "must be an object");
                }
                fields(statblock, Set.of("name", "isNpc", "source", "page", "size", "objectType",
                        "creatureType", "hp", "speed", "immune", "conditionImmune"), parser,
                        path + ".statblockData");
                if (statblock.has("speed") && !statblock.get("speed").isNull()) {
                    fields(statblock.get("speed"), Set.of("walk"), parser, path + ".statblockData.speed");
                }
                yield new RewardInlineStatblockEntryDte(
                        parser.getCodec().treeToValue(node.get("statblockData"),
                                RewardInlineObjectStatblockDte.class));
            }
            case "entries", "itemSub", "variantSub", "variantInner", "itemSpell" -> {
                fields(node, Set.of("type", "name", "entry", "entries", "source", "page", "data", "id"),
                        parser, path);
                yield new NamedEntryDte(
                        type,
                        string(node, "name", parser, path, false),
                        string(node, "entry", parser, path, false),
                        children(node, "entries", parser, path),
                        string(node, "source", parser, path, false), integer(node, "page", parser, path),
                        entryMetadata(node.get("data"), parser, path + ".data"),
                        string(node, "id", parser, path, false));
            }
            case "list" -> {
                fields(node, Set.of("type", "style", "columns", "items", "name"), parser, path);
                yield new ListEntryDte(string(node, "style", parser, path, false),
                        integer(node, "columns", parser, path), children(node, "items", parser, path),
                        string(node, "name", parser, path, false));
            }
            case "item" -> {
                fields(node, Set.of("type", "name", "entry", "entries"), parser, path);
                String entry = string(node, "entry", parser, path, false);
                if (node.has("entry") && node.has("entries")) {
                    throw error(parser, path, "cannot contain both entry and entries");
                }
                yield new ListItemEntryDte(
                        string(node, "name", parser, path, false), entry, children(node, "entries", parser, path));
            }
            case "table" -> {
                fields(node, Set.of("type", "caption", "colLabels", "colStyles", "rows", "source", "page",
                        "footnotes", "data", "srd52", "isNameGenerator"), parser, path);
                yield new TableEntryDte(
                        string(node, "caption", parser, path, false), strings(node, "colLabels", parser, path),
                        strings(node, "colStyles", parser, path),
                        EntryTableCellDeserializer.readRows(node.get("rows"), parser, path + ".rows"),
                        string(node, "source", parser, path, false), integer(node, "page", parser, path),
                        strings(node, "footnotes", parser, path),
                        tableMetadata(node.get("data"), parser, path + ".data"),
                        bool(node, "srd52", parser, path),
                        bool(node, "isNameGenerator", parser, path));
            }
            case "quote" -> {
                fields(node, Set.of("type", "by", "entries"), parser, path);
                yield new QuoteEntryDte(
                        string(node, "by", parser, path, false), children(node, "entries", parser, path));
            }
            case "inset" -> {
                fields(node, Set.of("type", "name", "entries", "source", "page", "data"), parser, path);
                yield new InsetEntryDte(string(node, "name", parser, path, false), children(node, "entries", parser, path),
                        string(node, "source", parser, path, false), integer(node, "page", parser, path),
                        entryMetadata(node.get("data"), parser, path + ".data"));
            }
            case "section" -> {
                fields(node, Set.of("type", "name", "id", "entries"), parser, path);
                yield new SectionEntryDte(string(node, "name", parser, path, false),
                        string(node, "id", parser, path, false),
                        children(node, "entries", parser, path));
            }
            case "inline" -> {
                fields(node, Set.of("type", "entries"), parser, path);
                yield new InlineEntryDte(children(node, "entries", parser, path));
            }
            case "link" -> {
                fields(node, Set.of("type", "entry", "href"), parser, path);
                yield new LinkEntryDte(
                        string(node, "entry", parser, path, true),
                        href(node.get("href"), parser, path + ".href"));
            }
            case "image" -> {
                fields(node, Set.of("type", "href", "title", "width", "height", "credit"), parser, path);
                yield new ImageEntryDte(
                        href(node.get("href"), parser, path + ".href"),
                        string(node, "title", parser, path, false),
                        integer(node, "width", parser, path),
                        integer(node, "height", parser, path),
                        string(node, "credit", parser, path, false));
            }
            case "insetReadaloud" -> {
                fields(node, Set.of("type", "name", "entries"), parser, path);
                yield new ReadaloudEntryDte(
                        string(node, "name", parser, path, false),
                        children(node, "entries", parser, path));
            }
            default -> throw error(parser, path, "unsupported entry type '" + type + "'");
        };
    }

    private static EntryHrefDte href(JsonNode node, JsonParser parser, String path) throws IOException {
        if (node == null || !node.isObject()) {
            throw error(parser, path, "must be an object");
        }
        fields(node, Set.of("type", "path", "hash"), parser, path);
        return new EntryHrefDte(
                string(node, "type", parser, path, true),
                string(node, "path", parser, path, true),
                string(node, "hash", parser, path, false));
    }

    private static List<EntryDte> children(JsonNode node, String key, JsonParser p, String path) throws IOException {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isArray()) {
            throw error(p, path + "." + key, "must be an array");
        }
        List<EntryDte> out = new ArrayList<>();
        int i = 0;
        for (JsonNode child : value) {
            out.add(read(child, p, path + "." + key + "[" + (i++) + "]"));
        }
        return List.copyOf(out);
    }

    private static List<String> strings(JsonNode node, String key, JsonParser p, String path) throws IOException {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isArray()) {
            throw error(p, path + "." + key, "must be an array");
        }
        List<String> out = new ArrayList<>();
        int i = 0;
        for (JsonNode x : value) {
            if (!x.isTextual()) {
                throw error(p, path + "." + key + "[" + i + "]", "must be a string");
            }
            out.add(x.textValue());
            i++;
        }
        return List.copyOf(out);
    }

    static String string(JsonNode node, String key, JsonParser p, String path, boolean required) throws IOException {
        JsonNode v = node.get(key);
        if (v == null || v.isNull()) {
            if (required) {
                throw error(p, path + "." + key, "is required");
            }
            return null;
        }
        if (!v.isTextual()) {
            throw error(p, path + "." + key, "must be a string");
        }
        return v.textValue();
    }

    private static Integer integer(JsonNode node, String key, JsonParser p, String path) throws IOException {
        JsonNode v = node.get(key);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isIntegralNumber()) {
            throw error(p, path + "." + key, "must be an integer");
        }
        return v.intValue();
    }

    private static Boolean bool(JsonNode node, String key, JsonParser parser, String path) throws IOException {
        JsonNode value = node.get(key);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isBoolean()) {
            throw error(parser, path + "." + key, "must be a boolean");
        }
        return value.booleanValue();
    }

    private static EntryTableMetadataDte tableMetadata(JsonNode node, JsonParser parser, String path)
            throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        fields(node, Set.of("genTables"), parser, path);
        JsonNode generated = node.get("genTables");
        if (generated == null || generated.isNull()) {
            return new EntryTableMetadataDte(null);
        }
        fields(generated, Set.of("reprintedAs", "tableInclude", "tableName"), parser, path + ".genTables");
        List<String> reprintedAs = strings(generated, "reprintedAs", parser, path + ".genTables");
        Boolean tableInclude = null;
        JsonNode include = generated.get("tableInclude");
        if (include != null && !include.isNull()) {
            if (!include.isBoolean()) {
                throw error(parser, path + ".genTables.tableInclude", "must be a boolean");
            }
            tableInclude = include.booleanValue();
        }
        String tableName = string(generated, "tableName", parser, path + ".genTables", false);
        return new EntryTableMetadataDte(new EntryGeneratedTablesDte(reprintedAs, tableInclude, tableName));
    }

    private static EntryDataDte entryMetadata(JsonNode node, JsonParser parser, String path) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        fields(node, Set.of("isFeature", "overwrite"), parser, path);
        return new EntryDataDte(bool(node, "isFeature", parser, path), string(node, "overwrite", parser, path, false));
    }

    static void fields(JsonNode node, Set<String> allowed, JsonParser p, String path) throws IOException {
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!allowed.contains(name)) {
                throw error(p, path + "." + name, "unexpected property");
            }
        }
    }

    static JsonMappingException error(JsonParser p, String path, String message) {
        return JsonMappingException.from(p, path + " " + message);
    }
}
