package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterSpeedDeserializer extends JsonDeserializer<MonsterSpeedDte> {
    @Override
    public MonsterSpeedDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.speed", "must be an object");
        }
        MonsterJson.fields(n, Set.of("walk", "burrow", "climb", "fly", "swim", "canHover", "alternate", "choose", "note"), p, "$.speed");
        var codec = p.getCodec();
        return new MonsterSpeedDte(mode(n, "walk", p), mode(n, "burrow", p), mode(n, "climb", p), mode(n, "fly", p), mode(n, "swim", p),
                MonsterJson.bool(n, "canHover", p, "$.speed"), n.hasNonNull("alternate") ? codec.treeToValue(n.get("alternate"), MonsterAlternateSpeedDte.class) : null,
                n.hasNonNull("choose") ? codec.treeToValue(n.get("choose"), MonsterSpeedChoiceDte.class) : null, MonsterJson.text(n, "note", p, "$.speed"));
    }

    private MonsterSpeedModeDte mode(JsonNode n, String k, JsonParser p) throws IOException {
        return n.hasNonNull(k) ? p.getCodec().treeToValue(n.get(k), MonsterSpeedModeDte.class) : null;
    }
}
