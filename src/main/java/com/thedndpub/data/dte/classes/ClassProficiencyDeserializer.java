package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassProficiencyDeserializer extends JsonDeserializer<ClassProficiencyDte> {
    @Override
    public ClassProficiencyDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ClassProficiencyDte(node.textValue(), null, null, true);
        }
        fields(node, parser, "proficiency", "full", "optional");
        return new ClassProficiencyDte(text(node, "proficiency", parser),
                text(node, "full", parser), bool(node, "optional", parser), false);
    }
}
