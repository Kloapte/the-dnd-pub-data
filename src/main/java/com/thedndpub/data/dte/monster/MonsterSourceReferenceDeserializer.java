package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterSourceReferenceDeserializer extends JsonDeserializer<MonsterSourceReferenceDte> {
    @Override
    public MonsterSourceReferenceDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterSourceTextDte(n.textValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.sourceReferences[]", "must be string or object");
        }
        MonsterJson.fields(n, Set.of("source", "page"), p, "$.sourceReferences[]");
        return new MonsterSourceDetailsDte(MonsterJson.text(n, "source", p, "$.sourceReferences[]"), MonsterJson.integer(n, "page", p, "$.sourceReferences[]"));
    }
}
