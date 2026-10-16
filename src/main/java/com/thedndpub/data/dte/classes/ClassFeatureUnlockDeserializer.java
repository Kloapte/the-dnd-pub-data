package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassFeatureUnlockDeserializer extends JsonDeserializer<ClassFeatureUnlockDte> {
    @Override
    public ClassFeatureUnlockDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassFeatureUnlockDte(node.textValue(), null, null, null, true);
        }
        fields(node, parser, "classFeature", "gainSubclassFeature", "gainSubclassFeatureHasContent", "tableDisplayName");
        return new ClassFeatureUnlockDte(text(node, "classFeature", parser),
                bool(node, "gainSubclassFeature", parser), bool(node, "gainSubclassFeatureHasContent", parser),
                text(node, "tableDisplayName", parser), false);
    }
}
