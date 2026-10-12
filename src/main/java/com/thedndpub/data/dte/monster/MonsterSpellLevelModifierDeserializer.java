package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;

public final class MonsterSpellLevelModifierDeserializer extends JsonDeserializer<MonsterSpellLevelModifierDte> {
    @Override
    public MonsterSpellLevelModifierDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isObject() && (n.has("slots") || n.has("lower") || n.has("spells"))) {
            return new MonsterSpellLevelModifierValueDte(p.getCodec().treeToValue(n, MonsterSpellLevelDte.class));
        }
        if (n.isArray()) {
            var out = new ArrayList<MonsterSpellReplacementDte>();
            for (JsonNode x : n) {
                if (!x.isObject()) {
                    throw MonsterJson.error(p, "$._mod.spells[]", "must be a replacement object");
                }
                MonsterJson.fields(x, java.util.Set.of("replace", "with"), p, "$._mod.spells[]");
                out.add(new MonsterSpellReplacementDte(MonsterJson.text(x, "replace", p, "$._mod.spells[]"), MonsterJson.text(x, "with", p, "$._mod.spells[]")));
            }
            return new MonsterSpellLevelModifierListDte(java.util.List.copyOf(out));
        }
        throw MonsterJson.error(p, "$._mod.spells", "must be a spell level or replacement array");
    }
}
