package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class FeatSenseGrantDeserializer extends StdDeserializer<FeatSenseGrantDte> {
    public FeatSenseGrantDeserializer() {
        super(FeatSenseGrantDte.class);
    }

    @Override
    public FeatSenseGrantDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (!node.isObject()) {
            throw context.weirdStringException(node.toString(), FeatSenseGrantDte.class, "Expected an object");
        }
        Map<String, Integer> ranges = new TreeMap<>();
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            if (!field.getValue().isIntegralNumber()) {
                throw context.weirdStringException(field.getValue().toString(), Integer.class,
                        "Sense ranges must be integers");
            }
            ranges.put(field.getKey(), field.getValue().intValue());
        }
        return new FeatSenseGrantDte(Collections.unmodifiableMap(ranges));
    }
}
