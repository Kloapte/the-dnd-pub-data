package com.thedndpub.data.dte.generic.deserializers;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ValueDeserializer;

import java.time.LocalDate;

public class LocalDateDeserializer extends ValueDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);

        if(node.isTextual()) {
            return LocalDate.parse(node.asText());
        }
        else {
            throw context.weirdStringException(
                    node.toString(),
                    String.class,
                    "Not a string so can't be LocalDate"
            );
        }
    }
}