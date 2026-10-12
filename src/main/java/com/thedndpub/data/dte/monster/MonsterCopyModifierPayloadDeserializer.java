package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;

public final class MonsterCopyModifierPayloadDeserializer extends JsonDeserializer<MonsterCopyModifierPayloadDte> {
    @Override
    public MonsterCopyModifierPayloadDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterCopyTextPayloadDte(n.textValue());
        }
        if (n.isArray()) {
            if (n.isEmpty() || n.get(0).isTextual()) {
                return new MonsterCopyTextListPayloadDte(MonsterJson.stringsNode(n, p, "$._copy._mod.items"));
            }
            if (n.get(0).isObject() && spellcasting(n.get(0))) {
                var values = new ArrayList<MonsterSpellcastingDte>();
                for (JsonNode x : n) {
                    values.add(p.getCodec().treeToValue(x, MonsterSpellcastingDte.class));
                }
                return new MonsterCopySpellcastingListPayloadDte(java.util.List.copyOf(values));
            }
            var values = new ArrayList<MonsterFeatureDte>();
            for (JsonNode x : n) {
                values.add(p.getCodec().treeToValue(x, MonsterFeatureDte.class));
            }
            return new MonsterCopyFeatureListPayloadDte(java.util.List.copyOf(values));
        }
        if (n.isObject()) {
            if (spellcasting(n)) {
                return new MonsterCopySpellcastingPayloadDte(p.getCodec().treeToValue(n, MonsterSpellcastingDte.class));
            }
            if (n.has("resist") || n.has("immune") || n.has("vulnerable") || n.has("conditionImmune")) {
                return new MonsterCopyDefensePayloadDte(p.getCodec().treeToValue(n, MonsterDefenseDte.class));
            }
            return new MonsterCopyFeaturePayloadDte(p.getCodec().treeToValue(n, MonsterFeatureDte.class));
        }
        throw MonsterJson.error(p, "$._copy._mod.items", "must be string, object, or array");
    }

    private boolean spellcasting(JsonNode n) {
        return "spellcasting".equals(n.path("type").asText()) || n.has("will") || n.has("daily") || n.has("spells") || n.has("headerEntries");
    }
}
