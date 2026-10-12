package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

/**
 * Handles the observed string/object material form and the boolean/string consume member.
 */
public final class MaterialComponentDeserializer extends JsonDeserializer<MaterialComponentDte> {
    @Override
    public MaterialComponentDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new MaterialTextDte(parser.getText());
        }
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw MismatchedInputException.from(parser, MaterialComponentDte.class,
                    "spell.components.m must be a string or object; got " + parser.currentToken());
        }
        JsonNode node = parser.getCodec().readTree(parser);
        String text = requiredText(node, "text", parser);
        Integer cost = node.hasNonNull("cost") ? node.get("cost").intValue() : null;
        ConsumeDte consume = null;
        JsonNode consumeNode = node.get("consume");
        if (consumeNode != null && !consumeNode.isNull()) {
            if (consumeNode.isBoolean()) {
                consume = new ConsumeFlagDte(consumeNode.booleanValue());
            } else if (consumeNode.isTextual()) {
                consume = new ConsumeNoteDte(consumeNode.textValue());
            } else {
                throw MismatchedInputException.from(parser, ConsumeDte.class,
                        "spell.components.m.consume must be boolean or string; got " + consumeNode.getNodeType());
            }
        }
        rejectUnknownFields(node, parser);
        return new MaterialDetailsDte(text, cost, consume);
    }

    private static String requiredText(JsonNode node, String field, JsonParser parser) throws IOException {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual()) {
            throw MismatchedInputException.from(parser, MaterialDetailsDte.class,
                    "spell.components.m.text must be a string");
        }
        return value.textValue();
    }

    private static void rejectUnknownFields(JsonNode node, JsonParser parser) throws IOException {
        var fields = node.fieldNames();
        while (fields.hasNext()) {
            String field = fields.next();
            if (!field.equals("text") && !field.equals("cost") && !field.equals("consume")) {
                throw MismatchedInputException.from(parser, MaterialDetailsDte.class,
                        "Unexpected spell.components.m property '" + field + "'");
            }
        }
    }
}
