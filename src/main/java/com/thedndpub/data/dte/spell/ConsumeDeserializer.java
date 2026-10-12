package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.io.IOException;

public final class ConsumeDeserializer extends JsonDeserializer<ConsumeDte> {
    @Override
    public ConsumeDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_TRUE || parser.currentToken() == JsonToken.VALUE_FALSE) {
            return new ConsumeFlagDte(parser.getBooleanValue());
        }
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new ConsumeNoteDte(parser.getText());
        }
        throw MismatchedInputException.from(parser, ConsumeDte.class,
                "spell.components.m.consume must be boolean or string; got " + parser.currentToken());
    }
}
