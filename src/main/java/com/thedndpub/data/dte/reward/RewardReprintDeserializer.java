package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Set;

public final class RewardReprintDeserializer extends JsonDeserializer<RewardReprintDte> {
    @Override
    public RewardReprintDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new RewardReprintTokenDte(node.textValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "reward reprint must be a string or tagged UID object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!Set.of("tag", "uid").contains(name)) {
                throw JsonMappingException.from(parser, "unexpected reward reprint property: " + name);
            }
        }
        if (!node.path("tag").isTextual() || !node.path("uid").isTextual()) {
            throw JsonMappingException.from(parser, "tagged reward reprint requires string tag and uid");
        }
        return new RewardReprintUidDte(node.path("tag").textValue(), node.path("uid").textValue());
    }
}
