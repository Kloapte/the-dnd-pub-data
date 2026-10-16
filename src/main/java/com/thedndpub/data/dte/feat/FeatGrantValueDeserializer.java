package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;

public final class FeatGrantValueDeserializer extends StdDeserializer<FeatGrantValueDte> {
    public FeatGrantValueDeserializer() {
        super(FeatGrantValueDte.class);
    }

    @Override
    public FeatGrantValueDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isBoolean()) {
            return new FeatBooleanGrantDte(node.booleanValue());
        }
        if (node.isIntegralNumber()) {
            return new FeatCountGrantDte(node.intValue());
        }
        if (node.isObject() && node.has("choose")) {
            FeatGrantChoiceDte choice = parser.getCodec().treeToValue(node.get("choose"), FeatGrantChoiceDte.class);
            return new FeatChoiceGrantDte(choice);
        }
        if (node.isObject() && (node.has("from") || node.has("count") || node.has("fromFilter"))) {
            FeatGrantChoiceDte choice = parser.getCodec().treeToValue(node, FeatGrantChoiceDte.class);
            return new FeatChoiceGrantDte(choice);
        }
        throw context.weirdStringException(node.toString(), FeatGrantValueDte.class,
                "Expected a boolean, integer, or choose object");
    }
}
