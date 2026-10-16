package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassSpellLevelDeserializer extends JsonDeserializer<ClassSpellLevelDte> {
    @Override
    public ClassSpellLevelDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isArray()) {
            return new ClassSpellLevelDte(list(node, ClassSpellSelectionDte.class, parser),
                    null, null, null, null, null);
        }
        fields(node, parser, "_", "daily", "rest", "resource", "ritual");
        return new ClassSpellLevelDte(null, list(node.get("_"), ClassSpellSelectionDte.class, parser),
                listsByKey(node.get("daily"), ClassSpellSelectionDte.class, parser),
                listsByKey(node.get("rest"), ClassSpellSelectionDte.class, parser),
                listsByKey(node.get("resource"), ClassSpellSelectionDte.class, parser),
                list(node.get("ritual"), ClassSpellSelectionDte.class, parser));
    }
}
