package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassSpellSelectionDeserializer extends JsonDeserializer<ClassSpellSelectionDte> {
    @Override
    public ClassSpellSelectionDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassSpellSelectionDte(node.textValue(), null, null, null);
        }
        fields(node, parser, "choose", "all", "count");
        if (node.has("choose") == node.has("all")) {
            throw error(parser, "A spell selector requires exactly one of choose or all");
        }
        if (node.has("choose") && node.get("choose").isNull()
                || node.has("all") && node.get("all").isNull()) {
            throw error(parser, "A spell selector cannot be null");
        }
        return new ClassSpellSelectionDte(null, read(node.get("choose"), ClassSpellChoiceDte.class, parser),
                text(node, "all", parser), integer(node, "count", parser));
    }
}
