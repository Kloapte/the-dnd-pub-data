package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;

public final class MonsterFeatureItemsDeserializer extends JsonDeserializer<MonsterFeatureItemsDte> {
    @Override
    public MonsterFeatureItemsDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isObject()) {
            return new MonsterFeatureItemDte(p.getCodec().treeToValue(n, MonsterFeatureDte.class));
        }
        if (n.isArray()) {
            var out = new ArrayList<MonsterFeatureDte>();
            for (JsonNode x : n) {
                out.add(p.getCodec().treeToValue(x, MonsterFeatureDte.class));
            }
            return new MonsterFeatureItemListDte(java.util.List.copyOf(out));
        }
        throw MonsterJson.error(p, "$._mod.items", "must be object or array");
    }
}
