package com.thedndpub.data.dte.magicvariant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MagicVariantStringValuesDeserializer extends JsonDeserializer<MagicVariantStringValuesDte> {
    @Override
    public MagicVariantStringValuesDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode value = parser.readValueAsTree();
        if (value.isTextual()) {
            return new MagicVariantStringValuesDte(List.of(value.textValue()));
        }
        if (!value.isArray()) {
            throw JsonMappingException.from(parser, "Expected a string or an array of strings");
        }
        List<String> values = new ArrayList<>();
        for (JsonNode element : value) {
            if (!element.isTextual()) {
                throw JsonMappingException.from(parser, "Expected every array value to be a string");
            }
            values.add(element.textValue());
        }
        return new MagicVariantStringValuesDte(values);
    }
}
