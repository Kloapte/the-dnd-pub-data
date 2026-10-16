package com.thedndpub.data.dte.action;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

public final class ActionReprintDeserializer extends JsonDeserializer<ActionReprintDte> {
    @Override
    public ActionReprintDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new ActionReprintDte(node.textValue(), null);
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "Action reprint must be a string or an object");
        }
        Iterator<String> names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!Set.of("uid", "tag").contains(name)) {
                throw JsonMappingException.from(parser, "Unknown action reprint property '" + name + "'");
            }
        }
        JsonNode uid = node.get("uid");
        JsonNode tag = node.get("tag");
        if (uid == null || !uid.isTextual()) {
            throw JsonMappingException.from(parser, "Action reprint uid must be a string");
        }
        if (tag != null && !tag.isTextual()) {
            throw JsonMappingException.from(parser, "Action reprint tag must be a string");
        }
        return new ActionReprintDte(uid.textValue(), tag == null ? null : tag.textValue());
    }
}
