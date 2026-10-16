package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public final class OptionalFeatureReprintDeserializer extends StdDeserializer<OptionalFeatureReprintDte> {
    public OptionalFeatureReprintDeserializer() {
        super(OptionalFeatureReprintDte.class);
    }

    @Override
    public OptionalFeatureReprintDte deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            return new OptionalFeatureReprintDte(parser.getValueAsString(), null);
        }
        if (parser.currentToken() != JsonToken.START_OBJECT) {
            throw JsonMappingException.from(parser, "Reprint reference must be a string or object");
        }

        String uid = null;
        String tag = null;
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String field = parser.currentName();
            JsonToken valueToken = parser.nextToken();
            if ("uid".equals(field)) {
                uid = requireString(parser, valueToken, field);
            } else if ("tag".equals(field)) {
                tag = requireString(parser, valueToken, field);
            } else {
                throw JsonMappingException.from(parser, "Unsupported reprint field: " + field);
            }
        }
        if (uid == null || uid.isBlank()) {
            throw JsonMappingException.from(parser, "Reprint object requires a nonblank uid");
        }
        return new OptionalFeatureReprintDte(uid, tag);
    }

    private static String requireString(JsonParser parser, JsonToken token, String field) throws IOException {
        if (token != JsonToken.VALUE_STRING) {
            throw JsonMappingException.from(parser, "Reprint field '" + field + "' must be a string");
        }
        return parser.getValueAsString();
    }
}
