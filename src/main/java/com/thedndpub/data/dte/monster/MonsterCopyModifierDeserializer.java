package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Set;

public final class MonsterCopyModifierDeserializer extends JsonDeserializer<MonsterCopyModifierDte> {
    @Override
    public MonsterCopyModifierDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$._copy._mod.instruction", "must be object");
        }
        MonsterJson.fields(n, Set.of("mode", "replace", "with", "flags", "names", "items", "renames", "index", "props", "prop", "skills", "value", "daily", "yearly", "spells", "will"), p, "$._copy._mod.instruction");
        var codec = p.getCodec();
        return new MonsterCopyModifierDte(MonsterJson.text(n, "mode", p, "$._copy._mod"), MonsterJson.text(n, "replace", p, "$._copy._mod"),
                MonsterJson.text(n, "with", p, "$._copy._mod"), MonsterJson.text(n, "flags", p, "$._copy._mod"),
                n.hasNonNull("names") ? codec.treeToValue(n.get("names"), MonsterNamesDte.class) : null,
                n.hasNonNull("items") ? codec.treeToValue(n.get("items"), MonsterCopyModifierPayloadDte.class) : null,
                nullSafeRename(n, p), MonsterJson.integer(n, "index", p, "$._copy._mod"), MonsterJson.strings(n, "props", p, "$._copy._mod"),
                MonsterJson.text(n, "prop", p, "$._copy._mod"), stringMap(n.get("skills"), p), MonsterJson.bool(n, "value", p, "$._copy._mod"),
                typed(n.get("daily"), p), typed(n.get("yearly"), p), levels(n.get("spells"), p), spellList(n.get("will"), p));
    }

    private MonsterFeatureRenameDte nullSafeRename(JsonNode n, JsonParser p) throws IOException {
        return n.hasNonNull("renames") ? p.getCodec().treeToValue(n.get("renames"), MonsterFeatureRenameDte.class) : null;
    }

    private java.util.Map<String, MonsterSkillValueDte> stringMap(JsonNode n, JsonParser p) throws IOException {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$._copy._mod.skills", "must be object");
        }
        var out = new java.util.LinkedHashMap<String, MonsterSkillValueDte>();
        var it = n.fields();
        while (it.hasNext()) {
            var e = it.next();
            out.put(e.getKey(), p.getCodec().treeToValue(e.getValue(), MonsterSkillValueDte.class));
        }
        return java.util.Map.copyOf(out);
    }

    private java.util.Map<String, java.util.List<MonsterSpellModificationDte>> typed(JsonNode n, JsonParser p) throws IOException {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$._copy._mod.spellUses", "must be object");
        }
        var out = new java.util.LinkedHashMap<String, java.util.List<MonsterSpellModificationDte>>();
        var it = n.fields();
        while (it.hasNext()) {
            var e = it.next();
            if (!e.getValue().isArray()) {
                throw MonsterJson.error(p, "$._copy._mod." + e.getKey(), "must be array");
            }
            var list = new java.util.ArrayList<MonsterSpellModificationDte>();
            for (JsonNode x : e.getValue()) {
                list.add(p.getCodec().treeToValue(x, MonsterSpellModificationDte.class));
            }
            out.put(e.getKey(), java.util.List.copyOf(list));
        }
        return java.util.Map.copyOf(out);
    }

    private java.util.Map<String, MonsterSpellLevelModifierDte> levels(JsonNode n, JsonParser p) throws IOException {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isObject()) {
            throw MonsterJson.error(p, "$._copy._mod.spells", "must be object");
        }
        var out = new java.util.LinkedHashMap<String, MonsterSpellLevelModifierDte>();
        var it = n.fields();
        while (it.hasNext()) {
            var e = it.next();
            out.put(e.getKey(), p.getCodec().treeToValue(e.getValue(), MonsterSpellLevelModifierDte.class));
        }
        return java.util.Map.copyOf(out);
    }

    private java.util.List<MonsterSpellModificationDte> spellList(JsonNode n, JsonParser p) throws IOException {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isArray()) {
            throw MonsterJson.error(p, "$._copy._mod.will", "must be array");
        }
        var out = new java.util.ArrayList<MonsterSpellModificationDte>();
        for (JsonNode x : n) {
            out.add(p.getCodec().treeToValue(x, MonsterSpellModificationDte.class));
        }
        return java.util.List.copyOf(out);
    }
}
