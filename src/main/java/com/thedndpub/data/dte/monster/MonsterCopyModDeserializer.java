package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MonsterCopyModDeserializer extends JsonDeserializer<MonsterCopyModDte> {
    @Override
    public MonsterCopyModDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$._copy._mod", "must be an object");
        }
        Map<String, MonsterCopyModifierSetDte> changes = new LinkedHashMap<>();
        var fields = n.fields();
        while (fields.hasNext()) {
            var field = fields.next();
            changes.put(field.getKey(), p.getCodec().treeToValue(field.getValue(), MonsterCopyModifierSetDte.class));
        }
        return new MonsterCopyModDte(java.util.Collections.unmodifiableMap(changes));
    }
}
