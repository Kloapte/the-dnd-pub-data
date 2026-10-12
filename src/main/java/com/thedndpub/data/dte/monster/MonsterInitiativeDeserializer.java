package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterInitiativeDeserializer extends JsonDeserializer<MonsterInitiativeDte> {
    @Override
    public MonsterInitiativeDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isIntegralNumber()) {
            return new MonsterInitiativeValueDte(n.intValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.initiative", "must be integer or object");
        }
        MonsterJson.fields(n, Set.of("proficiency", "advantageMode"), p, "$.initiative");
        return new MonsterInitiativeDetailsDte(MonsterJson.integer(n, "proficiency", p, "$.initiative"), MonsterJson.text(n, "advantageMode", p, "$.initiative"));
    }
}
