package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterGearDeserializer extends JsonDeserializer<MonsterGearDte> {
    @Override
    public MonsterGearDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterGearNameDte(n.textValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.gear[]", "must be string or item object");
        }
        MonsterJson.fields(n, Set.of("item", "displayName", "quantity"), p, "$.gear[]");
        return new MonsterGearDetailsDte(MonsterJson.text(n, "item", p, "$.gear[]"), MonsterJson.text(n, "displayName", p, "$.gear[]"), MonsterJson.integer(n, "quantity", p, "$.gear[]"));
    }
}
