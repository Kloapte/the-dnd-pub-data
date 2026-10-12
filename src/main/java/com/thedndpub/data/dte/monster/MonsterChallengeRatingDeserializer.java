package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterChallengeRatingDeserializer extends JsonDeserializer<MonsterChallengeRatingDte> {
    @Override
    public MonsterChallengeRatingDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterChallengeRatingValueDte(n.textValue());
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.cr", "must be a string or object");
        }
        MonsterJson.fields(n, Set.of("cr", "coven", "lair", "xp", "xpLair"), p, "$.cr");
        return new MonsterChallengeRatingDetailsDte(MonsterJson.text(n, "cr", p, "$.cr"), MonsterJson.text(n, "coven", p, "$.cr"),
                MonsterJson.text(n, "lair", p, "$.cr"), MonsterJson.integer(n, "xp", p, "$.cr"), MonsterJson.integer(n, "xpLair", p, "$.cr"));
    }
}
