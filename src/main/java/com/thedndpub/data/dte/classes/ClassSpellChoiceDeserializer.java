package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassSpellChoiceDeserializer extends JsonDeserializer<ClassSpellChoiceDte> {
    @Override
    public ClassSpellChoiceDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassSpellChoiceDte(node.textValue(), null, null);
        }
        fields(node, parser, "from", "count");
        if (!node.hasNonNull("from") || !node.get("from").isArray()) {
            throw error(parser, "An explicit spell choice requires a from array");
        }
        return new ClassSpellChoiceDte(null, list(node.get("from"), String.class, parser),
                integer(node, "count", parser));
    }
}
