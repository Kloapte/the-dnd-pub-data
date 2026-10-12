package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.io.IOException;

public final class MonsterEntryDeserializer extends JsonDeserializer<MonsterEntryDte> {
    @Override
    public MonsterEntryDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isObject() && n.path("type").isTextual() && n.path("type").textValue().equals("spellcasting")) {
            return new SpellcastingMonsterEntryDte(p.getCodec().treeToValue(n, MonsterSpellcastingDte.class));
        }
        return new SharedMonsterEntryDte(p.getCodec().treeToValue(n, EntryDte.class));
    }
}
