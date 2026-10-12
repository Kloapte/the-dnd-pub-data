package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterSpellReferenceDeserializer extends JsonDeserializer<MonsterSpellReferenceDte> {
    @Override
    public MonsterSpellReferenceDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterSpellTextDte(n.textValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.spellcasting.spell", "must be string or object");
        }
        MonsterJson.fields(n, Set.of("entry", "hidden"), p, "$.spellcasting.spell");
        return new MonsterSpellDetailsDte(MonsterJson.text(n, "entry", p, "$.spellcasting.spell"), MonsterJson.bool(n, "hidden", p, "$.spellcasting.spell"));
    }
}
