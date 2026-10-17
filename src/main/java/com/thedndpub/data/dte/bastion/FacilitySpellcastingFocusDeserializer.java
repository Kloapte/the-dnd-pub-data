package com.thedndpub.data.dte.bastion;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class FacilitySpellcastingFocusDeserializer extends JsonDeserializer<FacilitySpellcastingFocusDte> {
    @Override
    public FacilitySpellcastingFocusDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isBoolean()) {
            return new FacilitySpellcastingFocusFlagDte(node.booleanValue());
        }
        if (node.isArray()) {
            List<String> types = new ArrayList<>();
            for (JsonNode value : node) {
                if (!value.isTextual()) {
                    throw JsonMappingException.from(parser, "spellcastingFocus array entries must be strings");
                }
                types.add(value.textValue());
            }
            return new FacilitySpellcastingFocusTypesDte(types);
        }
        throw JsonMappingException.from(parser, "spellcastingFocus must be a boolean or a string array");
    }
}
