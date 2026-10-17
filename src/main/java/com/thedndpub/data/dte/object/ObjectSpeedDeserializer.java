package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public final class ObjectSpeedDeserializer extends JsonDeserializer<ObjectSpeedDte> {
    @Override
    public ObjectSpeedDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new ObjectNumericSpeedDte(node.intValue());
        }
        if (!node.isObject() || node.isEmpty()) {
            throw JsonMappingException.from(parser, "Object speed must be an integer or movement object");
        }
        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()) {
            String name = fields.next();
            if (!Set.of("walk", "burrow").contains(name) || !node.get(name).isIntegralNumber()) {
                throw JsonMappingException.from(parser, "Unsupported object movement field: " + name);
            }
        }
        return new ObjectMovementSpeedDte(integer(node, "walk"), integer(node, "burrow"));
    }

    private static Integer integer(JsonNode node, String name) {
        return node.has(name) ? node.get(name).intValue() : null;
    }
}
