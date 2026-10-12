package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class MonsterSkillValueDeserializer extends JsonDeserializer<MonsterSkillValueDte> {
    @Override
    public MonsterSkillValueDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterSkillBonusDte(n.textValue());
        }
        if (n.isIntegralNumber()) {
            return new MonsterSkillNumericDte(n.intValue());
        }
        if (n.isArray()) {
            var values = new ArrayList<MonsterSkillChoiceDte>();
            int i = 0;
            for (JsonNode v : n) {
                if (!v.isObject()) {
                    throw MonsterJson.error(p, "$.skill[][" + i + "]", "must be a choice object");
                }
                MonsterJson.fields(v, Set.of("oneOf"), p, "$.skill[][" + i + "]");
                JsonNode choices = v.get("oneOf");
                if (choices == null || !choices.isObject()) {
                    throw MonsterJson.error(p, "$.skill[][" + i + "].oneOf", "must be an object");
                }
                Map<String, String> map = new LinkedHashMap<>();
                var it = choices.fields();
                while (it.hasNext()) {
                    var e = it.next();
                    if (!e.getValue().isTextual()) {
                        throw MonsterJson.error(p, "$.skill[][" + i + "].oneOf." + e.getKey(), "must be a string");
                    }
                    map.put(e.getKey(), e.getValue().textValue());
                }
                values.add(new MonsterSkillChoiceDte(Map.copyOf(map)));
                i++;
            }
            return new MonsterSkillAlternativesDte(java.util.List.copyOf(values));
        }
        throw MonsterJson.error(p, "$.skill[]", "must be string or choice-object array");
    }
}
