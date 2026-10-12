package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public final class MonsterShortNameDeserializer extends JsonDeserializer<MonsterShortNameDte> {
    @Override
    public MonsterShortNameDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterShortNameTextDte(n.textValue());
        }
        if (n.isBoolean()) {
            return new MonsterShortNameFlagDte(n.booleanValue());
        }
        throw MonsterJson.error(p, "$.shortName", "must be string or boolean");
    }
}
