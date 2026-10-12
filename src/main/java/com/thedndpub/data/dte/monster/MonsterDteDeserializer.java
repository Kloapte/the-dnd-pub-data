package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.io.IOException;
import java.util.Set;

/**
 * Validates the flat source contract, then delegates each concern group to its typed record.
 */
public final class MonsterDteDeserializer extends JsonDeserializer<MonsterDte> {
    private static final Set<String> IGNORED = Set.of("hasFluff", "hasFluffImages", "soundClip");
    private static final Set<String> IDENTITY = Set.of(
            "name", "source", "page", "alias", "otherSources", "referenceSources", "reprintedAs", "group",
            "legendaryGroup", "size", "type", "alignment", "alignmentPrefix", "dragonAge", "dragonCastingColor",
            "shortName", "isNamedCreature", "isNpc", "level", "summonedByClass", "summonedBySpell",
            "summonedBySpellLevel");
    private static final Set<String> STATS = Set.of(
            "str", "dex", "con", "int", "wis", "cha", "initiative", "hp", "cr", "speed", "ac", "save",
            "skill", "passive", "senses", "languages", "savingThrowForced", "savingThrowForcedLegendary",
            "savingThrowForcedSpell");
    private static final Set<String> DEFENSES = Set.of("resist", "immune", "vulnerable", "conditionImmune");
    private static final Set<String> FEATURES = Set.of(
            "trait", "action", "bonus", "reaction", "legendary", "mythic", "variant", "spellcasting", "actionNote",
            "reactionNote", "legendaryHeader", "mythicHeader", "reactionHeader", "legendaryActions",
            "legendaryActionsLair");
    private static final Set<String> TAGS = Set.of(
            "actionTags", "damageTags", "damageTagsLegendary", "damageTagsSpell", "languageTags", "miscTags",
            "senseTags", "spellcastingTags", "traitTags", "conditionInflict", "conditionInflictLegendary",
            "conditionInflictSpell");
    private static final Set<String> SUPPLEMENTAL = Set.of(
            "environment", "gear", "attachedItems", "treasure", "familiar", "tokenCustom", "hasToken",
            "foundryTokenScale", "pbNote", "sizeNote", "altArt", "tokenCredit", "srd", "srd52", "basicRules",
            "basicRules2024", "isReprinted");
    private static final Set<String> ALLOWED = union(IDENTITY, STATS, DEFENSES, FEATURES, TAGS, SUPPLEMENTAL, IGNORED, Set.of("_copy", "_versions"));

    @Override
    public MonsterDte deserialize(JsonParser p, DeserializationContext c) throws IOException {
        JsonNode n = p.getCodec().readTree(p);
        if (!n.isObject()) {
            throw JsonMappingException.from(p, "$.monster must be an object");
        }
        var fields = n.fieldNames();
        while (fields.hasNext()) {
            String f = fields.next();
            if (!ALLOWED.contains(f)) {
                throw JsonMappingException.from(p, "$.monster." + f + " unexpected property");
            }
        }
        return new MonsterDte(read(n, IDENTITY, p, MonsterIdentityDte.class), read(n, STATS, p, MonsterStatisticsDte.class),
                read(n, DEFENSES, p, MonsterDefensesDte.class), read(n, FEATURES, p, MonsterFeaturesDte.class),
                read(n, TAGS, p, MonsterTagsDte.class), read(n, SUPPLEMENTAL, p, MonsterSupplementalDte.class),
                n.has("_copy") ? p.getCodec().treeToValue(n.get("_copy"), MonsterCopyReferenceDte.class) : null,
                versions(n.get("_versions"), p));
    }

    private java.util.List<MonsterVersionDte> versions(JsonNode n, JsonParser p) throws IOException {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isArray()) {
            throw JsonMappingException.from(p, "$._versions must be an array");
        }
        var out = new java.util.ArrayList<MonsterVersionDte>();
        for (JsonNode v : n) {
            out.add(p.getCodec().treeToValue(v, MonsterVersionDte.class));
        }
        return java.util.List.copyOf(out);
    }

    private <T> T read(JsonNode node, Set<String> names, JsonParser p, Class<T> type) throws IOException {
        ObjectNode filtered = JsonNodeFactory.instance.objectNode();
        for (String name : names) {
            if (node.has(name)) {
                filtered.set(name, node.get(name));
            }
        }
        return p.getCodec().treeToValue(filtered, type);
    }

    @SafeVarargs
    private static Set<String> union(Set<String>... sets) {
        java.util.HashSet<String> all = new java.util.HashSet<>();
        for (Set<String> s : sets) {
            all.addAll(s);
        }
        return Set.copyOf(all);
    }
}
