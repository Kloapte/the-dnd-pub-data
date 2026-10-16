package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class FeatSpellGrantNodeDeserializer extends StdDeserializer<FeatSpellGrantNodeDte> {
    public FeatSpellGrantNodeDeserializer() {
        super(FeatSpellGrantNodeDte.class);
    }

    @Override
    public FeatSpellGrantNodeDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return readNode(parser.getCodec().readTree(parser), parser, context);
    }

    private FeatSpellGrantNodeDte readNode(JsonNode node, JsonParser parser, DeserializationContext context)
            throws IOException {
        if (node.isTextual()) {
            return new FeatSpellTextNodeDte(node.textValue());
        }
        if (node.isIntegralNumber()) {
            return new FeatSpellCountNodeDte(node.intValue());
        }
        if (node.isBoolean()) {
            return new FeatSpellFlagNodeDte(node.booleanValue());
        }
        if (node.isArray()) {
            var values = new ArrayList<FeatSpellGrantNodeDte>();
            for (JsonNode item : node) {
                values.add(readNode(item, parser, context));
            }
            return new FeatSpellArrayNodeDte(java.util.List.copyOf(values));
        }
        if (node.isObject()) {
            Map<String, FeatSpellGrantNodeDte> values = new TreeMap<>();
            var fields = node.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                values.put(field.getKey(), readNode(field.getValue(), parser, context));
            }
            return new FeatSpellObjectNodeDte(Collections.unmodifiableMap(values));
        }
        throw context.weirdStringException(node.toString(), FeatSpellGrantNodeDte.class,
                "Unsupported value in structured spell grant");
    }
}
