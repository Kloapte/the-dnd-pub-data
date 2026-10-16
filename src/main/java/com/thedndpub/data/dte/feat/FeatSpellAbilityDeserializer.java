package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;

public final class FeatSpellAbilityDeserializer extends StdDeserializer<FeatSpellAbilityDte> {
    public FeatSpellAbilityDeserializer() {
        super(FeatSpellAbilityDte.class);
    }

    @Override
    public FeatSpellAbilityDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isTextual()) {
            return new FeatSpellAbilityNameDte(node.textValue());
        }
        if (node.isArray()) {
            return new FeatSpellAbilityChoiceDte(readStrings(node, parser, context));
        }
        if (node.isObject() && node.size() == 1 && node.has("choose")) {
            return new FeatSpellAbilityChoiceDte(readStrings(node.get("choose"), parser, context));
        }
        throw context.weirdStringException(node.toString(), FeatSpellAbilityDte.class,
                "Expected an ability name or choice object");
    }

    private static java.util.List<String> readStrings(
            JsonNode node,
            JsonParser parser,
            DeserializationContext context) throws IOException {
        if (!node.isArray()) {
            throw context.weirdStringException(node.toString(), FeatSpellAbilityChoiceDte.class,
                    "Ability choices must be an array");
        }
        var values = new ArrayList<String>();
        for (JsonNode value : node) {
            if (!value.isTextual()) {
                throw context.weirdStringException(value.toString(), String.class,
                        "Ability choices must contain strings");
            }
            values.add(value.textValue());
        }
        return java.util.List.copyOf(values);
    }
}
