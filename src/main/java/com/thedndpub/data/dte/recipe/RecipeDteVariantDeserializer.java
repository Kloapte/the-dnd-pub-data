package com.thedndpub.data.dte.recipe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;

abstract class RecipeDteVariantDeserializer<T> extends JsonDeserializer<T> {
    protected abstract Class<? extends T> typeForDiscriminator(String discriminator);

    protected T textValue(String value) throws IOException {
        throw new IOException("String value is not supported for " + getClass().getSimpleName());
    }

    @Override
    public final T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return textValue(parser.getText());
        }
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            return context.reportInputMismatch(this, "Expected string or object recipe entry");
        }

        TokenBuffer buffer = new TokenBuffer(parser, context);
        buffer.copyCurrentStructure(parser);
        String discriminator = findDiscriminator(buffer, parser);
        Class<? extends T> concreteType = typeForDiscriminator(discriminator);
        if (concreteType == null) {
            throw JsonMappingException.from(parser,
                    "Unsupported recipe source variant with discriminator '" + discriminator + "'");
        }

        try (JsonParser replay = buffer.asParser(parser)) {
            replay.nextToken();
            return context.readValue(replay, concreteType);
        }
    }

    private static String findDiscriminator(TokenBuffer buffer, JsonParser source) throws IOException {
        try (JsonParser parser = buffer.asParser(source)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                return null;
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = parser.currentName();
                JsonToken valueToken = parser.nextToken();
                if ("type".equals(fieldName)) {
                    return valueToken == JsonToken.VALUE_STRING ? parser.getText() : null;
                }
                parser.skipChildren();
            }
        }
        return null;
    }
}
