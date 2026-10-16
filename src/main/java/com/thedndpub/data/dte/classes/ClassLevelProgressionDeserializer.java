package com.thedndpub.data.dte.classes;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;

import static com.thedndpub.data.dte.classes.ClassJson.*;

public final class ClassLevelProgressionDeserializer extends JsonDeserializer<ClassLevelProgressionDte> {
    @Override
    public ClassLevelProgressionDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isArray()) {
            return new ClassLevelProgressionDte(list(node, Integer.class, parser), null);
        }
        if (!node.isObject()) {
            throw error(parser, "A progression must be a numeric array or level/count object");
        }
        Map<Integer, Integer> milestones = new LinkedHashMap<>();
        var fields = node.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            if (!field.getKey().matches("[1-9][0-9]*")) {
                throw error(parser, "Invalid progression level: " + field.getKey());
            }
            milestones.put(Integer.valueOf(field.getKey()), integer(field.getValue(), parser));
        }
        return new ClassLevelProgressionDte(null, milestones);
    }
}
