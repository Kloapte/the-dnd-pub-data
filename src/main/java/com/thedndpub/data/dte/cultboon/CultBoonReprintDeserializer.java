package com.thedndpub.data.dte.cultboon;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class CultBoonReprintDeserializer extends StdDeserializer<CultBoonReprintDte> {
    public CultBoonReprintDeserializer() {
        super(CultBoonReprintDte.class);
    }

    @Override
    public CultBoonReprintDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new CultBoonReprintUidDte(parser.getValueAsString());
        }
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "A cult/boon reprint must be a UID string or tagged object");
        }

        String uid = null;
        String tag = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            if (parser.currentToken() != JsonToken.FIELD_NAME) {
                throw JsonMappingException.from(parser, "Unexpected token in cult/boon reprint object");
            }
            String fieldName = parser.currentName();
            parser.nextToken();
            switch (fieldName) {
                case "uid" -> uid = parser.getValueAsString();
                case "tag" -> tag = parser.getValueAsString();
                default -> throw JsonMappingException.from(parser, "Unknown cult/boon reprint field: " + fieldName);
            }
        }
        if (uid == null || uid.isBlank()) {
            throw JsonMappingException.from(parser, "A tagged cult/boon reprint requires a non-empty uid");
        }
        return new CultBoonTaggedReprintDte(uid, tag);
    }
}
