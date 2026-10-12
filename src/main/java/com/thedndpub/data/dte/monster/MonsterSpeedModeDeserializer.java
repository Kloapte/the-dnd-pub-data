package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterSpeedModeDeserializer extends JsonDeserializer<MonsterSpeedModeDte> {
    @Override
    public MonsterSpeedModeDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isIntegralNumber()) {
            return new MonsterSpeedValueDte(n.intValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.speed", "movement must be integer or object");
        }
        MonsterJson.fields(n, Set.of("number", "condition"), p, "$.speed");
        return new MonsterSpeedDetailsDte(MonsterJson.integer(n, "number", p, "$.speed"), MonsterJson.text(n, "condition", p, "$.speed"));
    }
}
