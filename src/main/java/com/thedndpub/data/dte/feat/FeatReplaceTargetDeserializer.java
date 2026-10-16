package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class FeatReplaceTargetDeserializer extends StdDeserializer<FeatReplaceTargetDte> {
    public FeatReplaceTargetDeserializer() {
        super(FeatReplaceTargetDte.class);
    }

    @Override
    public FeatReplaceTargetDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new FeatReplaceNameDte(node.textValue());
        }
        if (node.isObject() && node.size() == 1 && node.has("index") && node.get("index").isInt()) {
            return new FeatReplaceIndexDte(node.get("index").intValue());
        }
        throw context.weirdStringException(node.toString(), FeatReplaceTargetDte.class,
                "Expected a replacement name or index object");
    }
}
