package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterSpellModificationDeserializer extends JsonDeserializer<MonsterSpellModificationDte> {
    @Override
    public MonsterSpellModificationDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterSpellModificationTextDte(n.textValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.spellcasting.spell", "must be string or object");
        }
        if (n.has("replace") || n.has("with")) {
            MonsterJson.fields(n, Set.of("replace", "with"), p, "$.spellcasting.spell");
            return new MonsterSpellReplacementDte(MonsterJson.text(n, "replace", p, "$.spellcasting.spell"), MonsterJson.text(n, "with", p, "$.spellcasting.spell"));
        }
        MonsterJson.fields(n, Set.of("entry", "hidden"), p, "$.spellcasting.spell");
        return new MonsterSpellModificationEntryDte(MonsterJson.text(n, "entry", p, "$.spellcasting.spell"), MonsterJson.bool(n, "hidden", p, "$.spellcasting.spell"));
    }
}
