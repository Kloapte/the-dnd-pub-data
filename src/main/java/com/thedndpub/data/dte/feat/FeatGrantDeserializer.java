package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class FeatGrantDeserializer extends StdDeserializer<FeatGrantDte> {
    public FeatGrantDeserializer() {
        super(FeatGrantDte.class);
    }

    @Override
    public FeatGrantDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (!node.isObject()) {
            throw context.weirdStringException(node.toString(), FeatGrantDte.class, "Expected an object");
        }
        Map<String, FeatGrantValueDte> values = new TreeMap<>();
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            FeatGrantValueDte value = parser.getCodec().treeToValue(field.getValue(), FeatGrantValueDte.class);
            values.put(field.getKey(), value);
        }
        return new FeatGrantDte(Collections.unmodifiableMap(values));
    }
}
