package com.thedndpub.data.dte.common.entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

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
            case "entries", "itemSub", "variantSub", "variantInner" -> {
                fields(node, Set.of("type", "name", "entry", "entries", "source", "page"), parser, path);
                yield new NamedEntryDte(type, string(node, "name", parser, path, false), string(node, "entry", parser, path, false), children(node, "entries", parser, path),
                        string(node, "source", parser, path, false), integer(node, "page", parser, path));
            }
            case "list" -> {
                fields(node, Set.of("type", "style", "items"), parser, path);
                yield new ListEntryDte(string(node, "style", parser, path, false), children(node, "items", parser, path));
            }
            case "item" -> {
                fields(node, Set.of("type", "name", "entry", "entries"), parser, path);
                String entry = string(node, "entry", parser, path, false);
                if (node.has("entry") && node.has("entries")) {
                    throw error(parser, path, "cannot contain both entry and entries");
                }
                yield new ListItemEntryDte(string(node, "name", parser, path, false), entry, children(node, "entries", parser, path));
            }
            case "table" -> {
                fields(node, Set.of("type", "caption", "colLabels", "colStyles", "rows", "source", "page"), parser, path);
                yield new TableEntryDte(string(node, "caption", parser, path, false), strings(node, "colLabels", parser, path),
                        strings(node, "colStyles", parser, path), EntryTableCellDeserializer.readRows(node.get("rows"), parser, path + ".rows"),
                        string(node, "source", parser, path, false), integer(node, "page", parser, path));
            }
            case "quote" -> {
                fields(node, Set.of("type", "by", "entries"), parser, path);
                yield new QuoteEntryDte(string(node, "by", parser, path, false), children(node, "entries", parser, path));
            }
            case "inset" -> {
                fields(node, Set.of("type", "name", "entries", "source", "page"), parser, path);
                yield new InsetEntryDte(string(node, "name", parser, path, false), children(node, "entries", parser, path),
                        string(node, "source", parser, path, false), integer(node, "page", parser, path));
            }
            default -> throw error(parser, path, "unsupported entry type '" + type + "'");
        };
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
