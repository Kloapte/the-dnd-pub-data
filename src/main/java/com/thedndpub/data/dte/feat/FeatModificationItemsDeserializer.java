package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FeatModificationItemsDeserializer extends StdDeserializer<FeatModificationItemsDte> {
    public FeatModificationItemsDeserializer() {
        super(FeatModificationItemsDte.class);
    }

    @Override
    public FeatModificationItemsDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isArray()) {
            List<String> values = new ArrayList<>();
            for (JsonNode item : node) {
                if (!item.isTextual()) {
                    throw context.weirdStringException(item.toString(), String.class,
                            "Modification text items must be strings");
                }
                values.add(item.textValue());
            }
            return new FeatModificationTextItemsDte(List.copyOf(values));
        }
        if (node.isObject()) {
            var names = node.fieldNames();
            while (names.hasNext()) {
                String field = names.next();
                if (!field.equals("name") && !field.equals("entries")) {
                    throw context.weirdStringException(field, FeatModificationItemsDte.class,
                            "Unexpected named modification item property");
                }
            }
            if (!node.has("name") || !node.get("name").isTextual()
                    || !node.has("entries") || !node.get("entries").isArray()) {
                throw context.weirdStringException(node.toString(), FeatModificationItemsDte.class,
                        "Expected named modification item with entries");
            }
            List<EntryDte> entries = new java.util.ArrayList<>();
            for (JsonNode entry : node.get("entries")) {
                entries.add(parser.getCodec().treeToValue(entry, EntryDte.class));
            }
            return new FeatModificationNamedItemsDte(node.get("name").textValue(), List.copyOf(entries));
        }
        throw context.weirdStringException(node.toString(), FeatModificationItemsDte.class,
                "Expected an entry object or text array");
    }

}
