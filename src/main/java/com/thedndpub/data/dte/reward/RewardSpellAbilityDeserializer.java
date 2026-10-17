package com.thedndpub.data.dte.reward;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public final class RewardSpellAbilityDeserializer extends JsonDeserializer<RewardSpellAbilityDte> {
    @Override
    public RewardSpellAbilityDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new RewardSpellAbilityCodeDte(node.textValue());
        }
        requireOnly(node, Set.of("choose"), parser);
        JsonNode choose = node.path("choose");
        if (!choose.isArray()) {
            throw JsonMappingException.from(parser, "reward spell ability choice requires choose array");
        }
        ArrayList<String> values = new ArrayList<>();
        for (JsonNode value : choose) {
            if (!value.isTextual()) {
                throw JsonMappingException.from(parser, "reward spell ability choices must be strings");
            }
            values.add(value.textValue());
        }
        return new RewardSpellAbilityChoiceDte(values);
    }

    static void requireOnly(JsonNode node, Set<String> allowed, JsonParser parser) throws JsonMappingException {
        if (!node.isObject()) {
            throw JsonMappingException.from(parser, "expected object for reward spell structure");
        }
        var names = node.fieldNames();
        while (names.hasNext()) {
            String name = names.next();
            if (!allowed.contains(name)) {
                throw JsonMappingException.from(parser, "unexpected reward spell property: " + name);
            }
        }
    }
}
