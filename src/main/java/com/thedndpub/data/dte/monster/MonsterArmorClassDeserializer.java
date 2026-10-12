package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterArmorClassDeserializer extends JsonDeserializer<MonsterArmorClassDte> {
    @Override
    public MonsterArmorClassDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isIntegralNumber()) {
            return new MonsterArmorClassValueDte(n.intValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.ac[]", "must be an integer or object");
        }
        MonsterJson.fields(n, Set.of("ac", "from", "condition", "braces", "special"), p, "$.ac[]");
        return new MonsterArmorClassDetailsDte(MonsterJson.integer(n, "ac", p, "$.ac[]"), MonsterJson.strings(n, "from", p, "$.ac[]"),
                MonsterJson.text(n, "condition", p, "$.ac[]"), MonsterJson.bool(n, "braces", p, "$.ac[]"), MonsterJson.text(n, "special", p, "$.ac[]"));
    }
}
