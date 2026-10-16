package com.thedndpub.data.dte.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class ItemPackContentDeserializer extends JsonDeserializer<ItemPackContentDte> {
    private static final Set<String> FIELDS = Set.of("item", "quantity", "special");

    @Override
    public ItemPackContentDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken().isScalarValue()) {
            return new ItemPackContentDte(parser.getValueAsString(), null, null);
        }
        JsonNode node = parser.readValueAsTree();
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Expected pack content as a UID or object");
        }
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!FIELDS.contains(field)) {
                throw JsonMappingException.from(parser, "Unsupported pack content field: " + field);
            }
        }
        JsonNode item = node.get("item");
        JsonNode quantity = node.get("quantity");
        JsonNode special = node.get("special");
        if (item != null && !item.isTextual() || quantity != null && !quantity.isIntegralNumber()
                || special != null && !special.isTextual()) {
            throw JsonMappingException.from(parser, "Invalid structured pack content");
        }
        if (item == null && special == null) {
            throw JsonMappingException.from(parser, "Pack content must identify an item or special text");
        }
        return new ItemPackContentDte(item == null ? null : item.textValue(), quantity == null ? null : quantity.intValue(),
                special == null ? null : special.textValue());
    }
}
