package com.thedndpub.data.dte.variantrule;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class VariantRuleReprintDeserializer extends JsonDeserializer<VariantRuleReprintDte> {
    @Override
    public VariantRuleReprintDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual() && !node.textValue().isBlank()) {
            return new VariantRuleReprintTokenDte(node.textValue());
        }
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "variant rule reprint must be a UID string or tagged UID object");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!Set.of("tag", "uid").contains(name)) {
                throw JsonMappingException.from(parser, "unexpected variant rule reprint property: " + name);
            }
        }
        if (!node.path("tag").isTextual() || node.path("tag").textValue().isBlank()
                || !node.path("uid").isTextual() || node.path("uid").textValue().isBlank()) {
            throw JsonMappingException.from(parser, "tagged variant rule reprint requires non-blank tag and uid");
        }
        return new VariantRuleReprintUidDte(node.path("tag").textValue(), node.path("uid").textValue());
    }
}
