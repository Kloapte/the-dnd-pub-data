package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public final class BackgroundGrantGroupDeserializer extends StdDeserializer<BackgroundGrantGroupDte> {
    public BackgroundGrantGroupDeserializer() {
        super(BackgroundGrantGroupDte.class);
    }

    @Override
    public BackgroundGrantGroupDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (!node.isObject()) {
            throw context.weirdStringException(node.toString(), BackgroundGrantGroupDte.class,
                    "Expected a grant package object");
        }
        ObjectMapper mapper = (ObjectMapper) parser.getCodec();
        Map<String, BackgroundGrantValueDte> grants = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        node.fields().forEachRemaining(field -> {
            try {
                grants.put(field.getKey(), mapper.treeToValue(field.getValue(), BackgroundGrantValueDte.class));
            } catch (IOException exception) {
                throw new IllegalArgumentException("Invalid background grant '" + field.getKey() + "'", exception);
            }
        });
        return new BackgroundGrantGroupDte(grants);
    }
}
