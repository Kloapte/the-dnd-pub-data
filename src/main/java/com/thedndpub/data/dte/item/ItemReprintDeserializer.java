package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public final class ItemReprintDeserializer extends JsonDeserializer<ItemReprintDte> {
    @Override
    public ItemReprintDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isScalarValue()) {
            return new ItemReprintDte(parser.getValueAsString(), null);
        }
        JsonNode node = parser.readValueAsTree();
        if (!node.isObject() || !node.hasNonNull("uid")) {
            throw JsonMappingException.from(parser, "Expected a reprint UID or object containing uid");
        }
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!field.equals("uid") && !field.equals("tag")) {
                throw JsonMappingException.from(parser, "Unsupported reprint property: " + field);
            }
        }
        return new ItemReprintDte(node.get("uid").asText(), node.path("tag").isMissingNode()
                ? null : node.path("tag").asText());
    }
}
