package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class FeatResistanceDeserializer extends StdDeserializer<FeatResistanceDte> {
    public FeatResistanceDeserializer() {
        super(FeatResistanceDte.class);
    }

    @Override
    public FeatResistanceDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new FeatNamedResistanceDte(node.textValue());
        }
        if (node.isObject() && node.size() == 1 && node.has("choose")) {
            JsonNode choice = node.get("choose");
            FeatGrantChoiceDte value = parser.getCodec().treeToValue(choice, FeatGrantChoiceDte.class);
            return new FeatChoiceResistanceDte(value);
        }
        throw context.weirdStringException(node.toString(), FeatResistanceDte.class,
                "Expected a named resistance or a choose object");
    }
}
