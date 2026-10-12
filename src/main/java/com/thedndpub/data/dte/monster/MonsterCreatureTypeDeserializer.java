package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class MonsterCreatureTypeDeserializer extends JsonDeserializer<MonsterCreatureTypeDte> {
    @Override
    public MonsterCreatureTypeDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (n.isTextual()) {
            return new MonsterCreatureTypeDte(n.textValue(), null, null, null, null, null, null);
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$.type", "must be string or object");
        }
        MonsterJson.fields(n, Set.of("type", "tags", "swarmSize", "sidekickHidden", "sidekickTags", "sidekickType"), p, "$.type");
        String type = null;
        MonsterCreatureTypeChoiceDte choice = null;
        JsonNode t = n.get("type");
        if (t != null && !t.isNull()) {
            if (t.isTextual()) {
                type = t.textValue();
            } else if (t.isObject()) {
                MonsterJson.fields(t, Set.of("choose"), p, "$.type.type");
                choice = new MonsterCreatureTypeChoiceDte(MonsterJson.strings(t, "choose", p, "$.type.type"));
            } else {
                throw MonsterJson.error(p, "$.type.type", "must be string or choice object");
            }
        }
        List<MonsterCreatureTypeTagDte> tags = null;
        JsonNode ts = n.get("tags");
        if (ts != null && !ts.isNull()) {
            if (!ts.isArray()) {
                throw MonsterJson.error(p, "$.type.tags", "must be an array");
            }
            tags = new ArrayList<>();
            int i = 0;
            for (JsonNode tag : ts) {
                if (tag.isTextual()) {
                    tags.add(new MonsterCreatureTypeTagDte(tag.textValue(), null, null));
                } else if (tag.isObject()) {
                    MonsterJson.fields(tag, Set.of("tag", "prefix", "prefixHidden"), p, "$.type.tags[" + i + "]");
                    tags.add(new MonsterCreatureTypeTagDte(MonsterJson.text(tag, "tag", p, "$.type.tags[" + i + "]"), MonsterJson.text(tag, "prefix", p, "$.type.tags[" + i + "]"), MonsterJson.bool(tag, "prefixHidden", p, "$.type.tags[" + i + "]")));
                } else {
                    throw MonsterJson.error(p, "$.type.tags[" + i + "]", "must be string or object");
                }
                i++;
            }
        }
        return new MonsterCreatureTypeDte(type, choice, tags, MonsterJson.text(n, "swarmSize", p, "$.type"), MonsterJson.bool(n, "sidekickHidden", p, "$.type"),
                MonsterJson.strings(n, "sidekickTags", p, "$.type"), MonsterJson.text(n, "sidekickType", p, "$.type"));
    }
}
