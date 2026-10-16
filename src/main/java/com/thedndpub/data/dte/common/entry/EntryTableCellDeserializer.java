package com.thedndpub.data.dte.common.entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class EntryTableCellDeserializer extends JsonDeserializer<EntryTableCellDte> {
    @Override
    public EntryTableCellDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return read(parser.getCodec().readTree(parser), parser, "$cell");
    }

    static EntryTableCellDte read(JsonNode node, JsonParser parser, String path) throws IOException {
        if (node.isTextual()) {
            return new TextTableCellDte(node.textValue());
        }
        if (node.isIntegralNumber()) {
            return new NumericTableCellDte(node.intValue());
        }
        if (!node.isObject()) {
            throw EntryDeserializer.error(parser, path, "must be a string or cell object");
        }
        String type = EntryDeserializer.string(node, "type", parser, path, true);
        if (!type.equals("cell") && !type.equals("entries")) {
            throw EntryDeserializer.error(parser, path, "unsupported table cell type '" + type + "'");
        }
        if (type.equals("cell") && node.has("roll")) {
            EntryDeserializer.fields(node, Set.of("type", "roll", "entry"), parser, path);
            JsonNode roll = node.get("roll");
            if (!roll.isObject()) {
                throw EntryDeserializer.error(parser, path + ".roll", "must be an object");
            }
            EntryDeserializer.fields(roll, Set.of("exact", "min", "max", "pad"), parser, path + ".roll");
            JsonNode entry = node.get("entry");
            EntryDte parsedEntry = entry == null || entry.isNull() ? null
                    : EntryDeserializer.read(entry, parser, path + ".entry");
            return new RollTableCellDte(type, new TableRollDte(integer(roll, "exact", parser, path + ".roll"),
                    integer(roll, "min", parser, path + ".roll"), integer(roll, "max", parser, path + ".roll"),
                    bool(roll, "pad", parser, path + ".roll")), parsedEntry);
        }
        EntryDeserializer.fields(node, Set.of("type", "name", "entries"), parser, path);
        JsonNode entries = node.get("entries");
        List<EntryDte> parsed = null;
        if (entries != null && !entries.isNull()) {
            if (!entries.isArray()) {
                throw EntryDeserializer.error(parser, path + ".entries", "must be an array");
            }
            parsed = new ArrayList<>();
            int i = 0;
            for (JsonNode entry : entries) {
                parsed.add(EntryDeserializer.read(entry, parser, path + ".entries[" + (i++) + "]"));
            }
            parsed = List.copyOf(parsed);
        }
        return new StructuredTableCellDte(type, EntryDeserializer.string(node, "name", parser, path, false), parsed);
    }

    static List<List<EntryTableCellDte>> readRows(JsonNode value, JsonParser parser, String path) throws IOException {
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isArray()) {
            throw EntryDeserializer.error(parser, path, "must be an array");
        }
        List<List<EntryTableCellDte>> rows = new ArrayList<>();
        int ri = 0;
        for (JsonNode row : value) {
            if (!row.isArray()) {
                throw EntryDeserializer.error(parser, path + "[" + ri + "]", "must be an array");
            }
            List<EntryTableCellDte> cells = new ArrayList<>();
            int ci = 0;
            for (JsonNode cell : row) {
                cells.add(read(cell, parser, path + "[" + ri + "][" + (ci++) + "]"));
            }
            rows.add(List.copyOf(cells));
            ri++;
        }
        return List.copyOf(rows);
    }

    private static Integer integer(JsonNode n, String k, JsonParser p, String path) throws IOException {
        JsonNode v = n.get(k);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isIntegralNumber()) {
            throw EntryDeserializer.error(p, path + "." + k, "must be an integer");
        }
        return v.intValue();
    }

    private static Boolean bool(JsonNode n, String k, JsonParser p, String path) throws IOException {
        JsonNode v = n.get(k);
        if (v == null || v.isNull()) {
            return null;
        }
        if (!v.isBoolean()) {
            throw EntryDeserializer.error(p, path + "." + k, "must be a boolean");
        }
        return v.booleanValue();
    }
}
