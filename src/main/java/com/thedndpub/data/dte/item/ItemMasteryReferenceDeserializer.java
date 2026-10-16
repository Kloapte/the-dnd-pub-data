package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public final class ItemMasteryReferenceDeserializer extends JsonDeserializer<ItemMasteryReferenceDte> {
    @Override
    public ItemMasteryReferenceDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isScalarValue()) {
            return new ItemMasteryReferenceDte(parser.getValueAsString(), null);
        }
        JsonNode node = parser.readValueAsTree();
        if (!node.isObject() || !node.hasNonNull("uid")) {
            throw JsonMappingException.from(parser, "Expected a mastery UID or object containing uid");
        }
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!field.equals("uid") && !field.equals("note")) {
                throw JsonMappingException.from(parser, "Unsupported item mastery reference field: " + field);
            }
        }
        return new ItemMasteryReferenceDte(node.get("uid").asText(), node.path("note").asText(null));
    }
}
