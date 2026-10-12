package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterAlignmentDeserializer extends JsonDeserializer<MonsterAlignmentDte> {
    @Override
    public MonsterAlignmentDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterAlignmentDte(n.textValue(), null, null, null, null);
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.alignment[]", "must be string or object");
        }
        MonsterJson.fields(n, Set.of("alignment", "chance", "special", "note"), p, "$.alignment[]");
        return new MonsterAlignmentDte(null, MonsterJson.strings(n, "alignment", p, "$.alignment[]"), MonsterJson.integer(n, "chance", p, "$.alignment[]"),
                MonsterJson.text(n, "special", p, "$.alignment[]"), MonsterJson.text(n, "note", p, "$.alignment[]"));
    }
}
