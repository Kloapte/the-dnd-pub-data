package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public final class MonsterNamesDeserializer extends JsonDeserializer<MonsterNamesDte> {
    @Override
    public MonsterNamesDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterNameDte(n.textValue());
        }
        if (n.isArray()) {
            return new MonsterNameListDte(MonsterJson.stringsNode(n, p, "$._mod.names"));
        }
        throw MonsterJson.error(p, "$._mod.names", "must be string or array");
    }
}
