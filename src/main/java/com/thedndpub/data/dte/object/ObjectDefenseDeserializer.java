package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;

public final class ObjectDefenseDeserializer extends JsonDeserializer<ObjectDefenseDte> {
    @Override
    public ObjectDefenseDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ObjectNamedDefenseDte(node.textValue());
        }
        if (node.isObject() && node.size() == 1 && node.has("special") && node.get("special").isTextual()) {
            return new ObjectSpecialDefenseDte(node.get("special").textValue());
        }
        throw JsonMappingException.from(parser, "Object defense must be a string or {special: string}");
    }
}
