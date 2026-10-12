package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public final class MonsterPassiveDeserializer extends JsonDeserializer<MonsterPassiveDte> {
    @Override
    public MonsterPassiveDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isIntegralNumber()) {
            return new MonsterPassiveValueDte(n.intValue());
        }
        if (n.isTextual()) {
            return new MonsterPassiveTextDte(n.textValue());
        }
        throw MonsterJson.error(p, "$.passive", "must be integer or string");
    }
}
