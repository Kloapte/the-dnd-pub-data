package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MonsterDefenseDeserializer extends JsonDeserializer<MonsterDefenseDte> {
    @Override
    public MonsterDefenseDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        return read(p.getCodec().readTree(p), p, "$.defense");
    }

    private MonsterDefenseDte read(JsonNode n, JsonParser p, String path) throws IOException {
        if (n.isTextual()) {
            return new MonsterDefenseNameDte(n.textValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, path, "must be string or conditional defense object");
        }
        Set<String> keys = Set.of("resist", "immune", "vulnerable", "conditionImmune", "cond", "note", "preNote", "postNote", "special");
        MonsterJson.fields(n, keys, p, path);
        return new MonsterDefenseDetailsDte(list(n, "resist", p, path), list(n, "immune", p, path), list(n, "vulnerable", p, path), list(n, "conditionImmune", p, path),
                MonsterJson.bool(n, "cond", p, path), MonsterJson.text(n, "note", p, path), MonsterJson.text(n, "preNote", p, path), MonsterJson.text(n, "postNote", p, path), MonsterJson.text(n, "special", p, path));
    }

    private List<MonsterDefenseDte> list(JsonNode n, String key, JsonParser p, String path) throws IOException {
        JsonNode value = n.get(key);
        if (value == null || value.isNull()) {
            return null;
        }
        if (!value.isArray()) {
            throw MonsterJson.error(p, path + "." + key, "must be an array");
        }
        List<MonsterDefenseDte> out = new ArrayList<>();
        int i = 0;
        for (JsonNode item : value) {
            out.add(read(item, p, path + "." + key + "[" + (i++) + "]"));
        }
        return List.copyOf(out);
    }
}
