package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;

public final class MonsterCopyModifierSetDeserializer extends JsonDeserializer<MonsterCopyModifierSetDte> {
    @Override
    public MonsterCopyModifierSetDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterCopyModifierTextDte(n.textValue());
        }
        if (n.isObject()) {
            return new MonsterCopyModifierOneDte(p.getCodec().treeToValue(n, MonsterCopyModifierDte.class));
        }
        if (n.isArray()) {
            var out = new ArrayList<MonsterCopyModifierDte>();
            for (JsonNode x : n) {
                out.add(p.getCodec().treeToValue(x, MonsterCopyModifierDte.class));
            }
            return new MonsterCopyModifierManyDte(java.util.List.copyOf(out));
        }
        throw MonsterJson.error(p, "$._copy._mod.value", "must be string, object, or array");
    }
}
