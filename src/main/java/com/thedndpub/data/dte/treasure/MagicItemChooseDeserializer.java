package com.thedndpub.data.dte.treasure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.util.List;

public final class MagicItemChooseDeserializer extends JsonDeserializer<MagicItemChooseDte> {
    @Override
    public MagicItemChooseDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "Magic-item choose value must be an object");
        }
        if (parser.nextToken() != JsonToken.FIELD_NAME) {
            throw JsonMappingException.from(parser, "Magic-item choose object must have exactly one variant");
        }

        String variant = parser.currentName();
        JsonToken valueToken = parser.nextToken();
        JavaType stringListType = context.getTypeFactory().constructCollectionType(List.class, String.class);
        MagicItemChooseDte choice = switch (variant) {
            case "fromItems" -> new FromItemsChoiceDte(context.readValue(parser, stringListType));
            case "fromGeneric" -> new FromGenericChoiceDte(context.readValue(parser, stringListType));
            case "fromGroup" -> new FromGroupChoiceDte(context.readValue(parser, stringListType));
            case "fromMatching" -> new FromMatchingChoiceDte(
                    context.readValue(parser, MagicItemMatchingFilterDte.class));
            default -> throw JsonMappingException.from(parser,
                    "Unknown magic-item choose variant: " + variant);
        };

        if (valueToken == JsonToken.VALUE_NULL || parser.nextToken() != JsonToken.END_OBJECT) {
            throw JsonMappingException.from(parser, "Magic-item choose object must contain exactly one non-null variant");
        }
        return choice;
    }
}
