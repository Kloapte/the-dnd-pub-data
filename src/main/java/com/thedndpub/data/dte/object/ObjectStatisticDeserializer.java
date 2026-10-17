package com.thedndpub.data.dte.object;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.Iterator;

public final class ObjectStatisticDeserializer extends JsonDeserializer<ObjectStatisticDte> {
    @Override
    public ObjectStatisticDte deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        if (node.isIntegralNumber()) {
            return new ObjectNumericStatisticDte(node.intValue());
        }
        if (node.isObject() && node.size() == 1 && node.has("special") && node.get("special").isTextual()) {
            return new ObjectSpecialStatisticDte(node.get("special").textValue());
        }
        throw JsonMappingException.from(parser, "Object statistic must be an integer or {special: string}");
    }
}
