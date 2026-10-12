package com.thedndpub.data.dto.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.monster.catalog.Catalog;
import com.thedndpub.data.dto.monster.catalog.GearReference;
import com.thedndpub.data.dto.monster.content.ContentArray;
import com.thedndpub.data.dto.monster.content.ContentBoolean;
import com.thedndpub.data.dto.monster.content.ContentNull;
import com.thedndpub.data.dto.monster.content.ContentNumber;
import com.thedndpub.data.dto.monster.content.ContentObject;
import com.thedndpub.data.dto.monster.content.ContentText;
import com.thedndpub.data.dto.monster.content.ContentValue;
import com.thedndpub.data.dto.monster.content.InsetContent;
import com.thedndpub.data.dto.monster.content.ListContent;
import com.thedndpub.data.dto.monster.content.ListItem;
import com.thedndpub.data.dto.monster.content.MonsterContentDto;
import com.thedndpub.data.dto.monster.content.PlainText;
import com.thedndpub.data.dto.monster.content.QuoteContent;
import com.thedndpub.data.dto.monster.content.RollCell;
import com.thedndpub.data.dto.monster.content.Section;
import com.thedndpub.data.dto.monster.content.SpellcastingContent;
import com.thedndpub.data.dto.monster.content.StructuredCell;
import com.thedndpub.data.dto.monster.content.TableCellDto;
import com.thedndpub.data.dto.monster.content.TableContent;
import com.thedndpub.data.dto.monster.content.TextCell;
import com.thedndpub.data.dto.monster.defenses.Defense;
import com.thedndpub.data.dto.monster.defenses.Defenses;
import com.thedndpub.data.dto.monster.features.Combat;
import com.thedndpub.data.dto.monster.features.Feature;
import com.thedndpub.data.dto.monster.features.Features;
import com.thedndpub.data.dto.monster.features.VariantReference;
import com.thedndpub.data.dto.monster.identity.Alignment;
import com.thedndpub.data.dto.monster.identity.CreatureType;
import com.thedndpub.data.dto.monster.identity.Identity;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.identity.Reference;
import com.thedndpub.data.dto.monster.identity.RulesAvailability;
import com.thedndpub.data.dto.monster.identity.ShortName;
import com.thedndpub.data.dto.monster.identity.Source;
import com.thedndpub.data.dto.monster.identity.SourceReference;
import com.thedndpub.data.dto.monster.identity.Summoning;
import com.thedndpub.data.dto.monster.identity.TypeTag;
import com.thedndpub.data.dto.monster.internal.MonsterClassification;
import com.thedndpub.data.dto.monster.spellcasting.SpellLevel;
import com.thedndpub.data.dto.monster.spellcasting.SpellReference;
import com.thedndpub.data.dto.monster.spellcasting.SpellcastingBlock;
import com.thedndpub.data.dto.monster.statistics.AbilityScores;
import com.thedndpub.data.dto.monster.statistics.AlternateMovement;
import com.thedndpub.data.dto.monster.statistics.ArmorClass;
import com.thedndpub.data.dto.monster.statistics.ChallengeRating;
import com.thedndpub.data.dto.monster.statistics.HitPoints;
import com.thedndpub.data.dto.monster.statistics.Initiative;
import com.thedndpub.data.dto.monster.statistics.Movement;
import com.thedndpub.data.dto.monster.statistics.MovementValue;
import com.thedndpub.data.dto.monster.statistics.NullableList;
import com.thedndpub.data.dto.monster.statistics.PassivePerception;
import com.thedndpub.data.dto.monster.statistics.SkillAlternatives;
import com.thedndpub.data.dto.monster.statistics.SkillBonus;
import com.thedndpub.data.dto.monster.statistics.SkillValue;
import com.thedndpub.data.dto.monster.statistics.Statistics;

import com.thedndpub.data.dte.monster.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Deterministic, strict projection from the resolver's authoritative content tree.
 */
public final class MonsterMapper {
    private static final Set<String> ROOTS = Set.of(
            "name", "source", "page", "alias", "otherSources", "referenceSources", "reprintedAs", "group",
            "legendaryGroup", "size", "type", "alignment", "alignmentPrefix", "dragonAge", "dragonCastingColor",
            "shortName", "isNamedCreature", "isNpc", "level", "summonedByClass", "summonedBySpell",
            "summonedBySpellLevel", "str", "dex", "con", "int", "wis", "cha", "initiative", "hp", "cr",
            "speed", "ac", "save", "skill", "passive", "senses", "languages", "savingThrowForced",
            "savingThrowForcedLegendary", "savingThrowForcedSpell", "resist", "immune", "vulnerable",
            "conditionImmune", "trait", "action", "bonus", "reaction", "legendary", "mythic", "variant",
            "spellcasting", "actionNote", "reactionNote", "legendaryHeader", "mythicHeader", "reactionHeader",
            "legendaryActions", "legendaryActionsLair", "actionTags", "damageTags", "damageTagsLegendary",
            "damageTagsSpell", "conditionInflict", "conditionInflictLegendary", "conditionInflictSpell",
            "languageTags", "miscTags", "senseTags", "spellcastingTags", "traitTags", "environment", "gear",
            "attachedItems", "treasure", "familiar", "tokenCustom", "hasToken", "foundryTokenScale", "pbNote",
            "sizeNote", "altArt", "tokenCredit", "srd", "srd52", "basicRules", "basicRules2024",
            "isReprinted", "hasFluff", "hasFluffImages", "soundClip");
    private final ObjectMapper mapper;

    public MonsterMapper(ObjectMapper mapper) {
        this.mapper = Objects.requireNonNull(mapper);
    }

    /**
     * Explicit root-field allowlist, also used by corpus coverage tests.
     */
    public static Set<String> mappedRootProperties() {
        return ROOTS;
    }

    public MonsterDto map(ResolvedMonster resolved) {
        return mapRecord(resolved).monster();
    }

    public List<MonsterDto> mapAll(List<ResolvedMonster> resolved) {
        return resolved.stream().sorted(ORDER).map(this::map).toList();
    }

    public List<NormalizedMonsterRecord> mapRecords(List<ResolvedMonster> resolved) {
        return resolved.stream().sorted(ORDER).map(this::mapRecord).toList();
    }

    private static final Comparator<ResolvedMonster> ORDER = Comparator.comparing(ResolvedMonster::identity)
            .thenComparing(m -> m.location().filename()).thenComparingInt(m -> m.location().arrayIndex());

    public NormalizedMonsterRecord mapRecord(ResolvedMonster r) {
        ObjectNode n = r.content();
        String path = "$";
        requireObject(n, path);
        for (Iterator<String> it = n.fieldNames(); it.hasNext(); ) {
            String k = it.next();
            if (!ROOTS.contains(k)) {
                fail(path + "." + k, "unexpected resolved source property");
            }
            if (Set.of("_copy", "_versions", "_mod", "_templates").contains(k)) {
                fail(path + "." + k, "unresolved source instruction");
            }
        }
        String name = text(req(n, "name", path), path + ".name");
        String source = text(req(n, "source", path), path + ".source");
        String idValue = Base64.getUrlEncoder().withoutPadding().encodeToString(
                (source.toLowerCase(Locale.ROOT) + "\u0000" + name.toLowerCase(Locale.ROOT))
                        .getBytes(StandardCharsets.UTF_8));
        MonsterId id = new MonsterId("5etools:" + idValue);
        RulesAvailability availability = availability(n);
        List<SourceReference> other = sourceRefs(n.get("otherSources"), path + ".otherSources");
        List<SourceReference> refs = sourceRefs(n.get("referenceSources"), path + ".referenceSources");
        Identity identity = new Identity(
                id,
                name,
                strings(n.get("alias"), path + ".alias"),
                new Source(
                        source,
                        integer(n.get("page"), path + ".page"),
                        other,
                        refs,
                        strings(n.get("reprintedAs"), path + ".reprintedAs"),
                        availability),
                strings(n.get("group"), path + ".group"),
                strings(n.get("size"), path + ".size"),
                creatureType(n.get("type"), path + ".type"),
                alignments(n.get("alignment"), path + ".alignment"),
                textOrNull(n.get("alignmentPrefix"), path + ".alignmentPrefix"),
                shortName(n.get("shortName"), path + ".shortName"),
                bool(n.get("isNamedCreature"), path + ".isNamedCreature"),
                bool(n.get("isNpc"), path + ".isNpc"),
                textOrNull(n.get("dragonAge"), path + ".dragonAge"),
                textOrNull(n.get("dragonCastingColor"), path + ".dragonCastingColor"),
                summoning(n, path),
                reference(n.get("legendaryGroup"), path + ".legendaryGroup"));
        Statistics stats = new Statistics(
                abilityScores(n, path),
                initiative(n.get("initiative"), path + ".initiative"),
                armorClasses(n.get("ac"), path + ".ac"),
                hitPoints(n.get("hp"), path + ".hp"),
                movement(n.get("speed"), path + ".speed"),
                challenge(n.get("cr"), path + ".cr"),
                null,
                stringMap(n.get("save"), path + ".save"),
                skills(n.get("skill"), path + ".skill"),
                passive(n.get("passive"), path + ".passive"),
                strings(n.get("senses"), path + ".senses"),
                nullableStrings(n, "languages", path),
                integer(n.get("level"), path),
                strings(n.get("savingThrowForced"), path),
                strings(n.get("savingThrowForcedLegendary"), path),
                strings(n.get("savingThrowForcedSpell"), path),
                textOrNull(n.get("pbNote"), path));
        Defenses defenses = defenses(n, path);
        Features features = features(n, path);
        Combat combat = combat(n, path);
        List<SpellcastingBlock> casting = spellcasting(n.get("spellcasting"), path + ".spellcasting");
        MonsterClassification classification = classification(n, path);
        Catalog catalog = catalog(n, path, availability);
        MonsterDto dto = new MonsterDto(identity, stats, defenses, features, combat, casting, catalog);
        MonsterVersionProvenance vp = r.provenance().generatedVersion();
        MonsterImportProvenance provenance = new MonsterImportProvenance(r.location().filename(), r.location().arrayIndex(), r.identity().name(), r.identity().source(), id(r.copyParent()),
                vp == null ? null : id(vp.containingMonster()), vp == null ? null : vp.versionDefinitionIndex(), vp == null ? null : vp.implementationIndex(),
                r.appliedTemplates().stream().map(this::id).toList(), vp == null ? Map.of() : vp.variables());
        return new NormalizedMonsterRecord(dto, provenance, classification);
    }

    private RulesAvailability availability(JsonNode n) {
        Boolean basicRules = bool(n.get("basicRules"), "$");
        Boolean basicRules2024 = bool(n.get("basicRules2024"), "$");
        Boolean srd = bool(n.get("srd"), "$");
        Boolean srd52 = bool(n.get("srd52"), "$");
        if (basicRules == null && basicRules2024 == null && srd == null && srd52 == null) {
            return null;
        }
        return new RulesAvailability(basicRules, basicRules2024, srd, srd52, Set.of());
    }

    private AbilityScores abilityScores(ObjectNode n, String path) {
        AbilityScores scores = new AbilityScores(
                integer(n.get("str"), path),
                integer(n.get("dex"), path),
                integer(n.get("con"), path),
                integer(n.get("int"), path),
                integer(n.get("wis"), path),
                integer(n.get("cha"), path));
        if (scores.strength() == null && scores.dexterity() == null && scores.constitution() == null
                && scores.intelligence() == null && scores.wisdom() == null && scores.charisma() == null) {
            return null;
        }
        return scores;
    }

    private Summoning summoning(ObjectNode n, String path) {
        String summonedByClass = textOrNull(n.get("summonedByClass"), path);
        String summonedBySpell = textOrNull(n.get("summonedBySpell"), path);
        Integer spellLevel = integer(n.get("summonedBySpellLevel"), path);
        if (isEmpty(summonedByClass) && isEmpty(summonedBySpell) && spellLevel == null) {
            return null;
        }
        return new Summoning(summonedByClass, summonedBySpell, spellLevel);
    }

    private Defenses defenses(ObjectNode n, String path) {
        NullableList<Defense> resistances = defenses(n, "resist", path);
        NullableList<Defense> immunities = defenses(n, "immune", path);
        NullableList<Defense> vulnerabilities = defenses(n, "vulnerable", path);
        NullableList<Defense> conditionImmunities = defenses(n, "conditionImmune", path);
        if (resistances == null && immunities == null && vulnerabilities == null && conditionImmunities == null) {
            return null;
        }
        return new Defenses(resistances, immunities, vulnerabilities, conditionImmunities);
    }

    private Features features(ObjectNode n, String path) {
        List<Feature> traits = features(n.get("trait"), path + ".trait");
        List<Feature> actions = features(n.get("action"), path + ".action");
        List<Feature> bonusActions = features(n.get("bonus"), path + ".bonus");
        List<Feature> reactions = features(n.get("reaction"), path + ".reaction");
        List<Feature> legendaryActions = features(n.get("legendary"), path + ".legendary");
        List<Feature> mythicActions = features(n.get("mythic"), path + ".mythic");
        List<Feature> variants = features(n.get("variant"), path + ".variant");
        String actionNote = textOrNull(n.get("actionNote"), path);
        String reactionNote = textOrNull(n.get("reactionNote"), path);
        List<String> legendaryHeader = strings(n.get("legendaryHeader"), path);
        List<String> mythicHeader = strings(n.get("mythicHeader"), path);
        List<String> reactionHeader = strings(n.get("reactionHeader"), path);

        if (traits.isEmpty() && actions.isEmpty() && bonusActions.isEmpty() && reactions.isEmpty()
                && legendaryActions.isEmpty() && mythicActions.isEmpty() && variants.isEmpty()
                && isEmpty(actionNote) && isEmpty(reactionNote) && legendaryHeader.isEmpty()
                && mythicHeader.isEmpty() && reactionHeader.isEmpty()) {
            return null;
        }

        return new Features(traits, actions, bonusActions, reactions, legendaryActions, mythicActions,
                variants, actionNote, reactionNote, legendaryHeader, mythicHeader, reactionHeader);
    }

    private Combat combat(ObjectNode n, String path) {
        Integer legendaryActions = integer(n.get("legendaryActions"), path);
        Integer legendaryActionsLair = integer(n.get("legendaryActionsLair"), path);
        if (legendaryActions == null && legendaryActionsLair == null) {
            return null;
        }
        return new Combat(legendaryActions, legendaryActionsLair);
    }

    private MonsterClassification classification(ObjectNode n, String path) {
        return new MonsterClassification(
                set(n.get("actionTags"), path),
                set(n.get("damageTags"), path),
                set(n.get("damageTagsLegendary"), path),
                set(n.get("damageTagsSpell"), path),
                set(n.get("conditionInflict"), path),
                set(n.get("conditionInflictLegendary"), path),
                set(n.get("conditionInflictSpell"), path),
                set(n.get("languageTags"), path),
                set(n.get("miscTags"), path),
                set(n.get("senseTags"), path),
                set(n.get("spellcastingTags"), path),
                set(n.get("traitTags"), path));
    }

    private Catalog catalog(ObjectNode n, String path, RulesAvailability availability) {
        Set<String> environments = set(n.get("environment"), path);
        List<GearReference> gear = gear(n.get("gear"), path);
        List<String> attachedItems = strings(n.get("attachedItems"), path);
        Set<String> treasure = set(n.get("treasure"), path);
        Boolean familiar = bool(n.get("familiar"), path);
        Boolean reprinted = bool(n.get("isReprinted"), path);

        if (environments.isEmpty() && gear.isEmpty() && attachedItems.isEmpty() && treasure.isEmpty()
                && familiar == null && availability == null && reprinted == null) {
            return null;
        }

        return new Catalog(environments, gear, attachedItems, treasure, familiar, availability, reprinted);
    }

    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private MonsterId id(MonsterIdentity identity) {
        if (identity == null) {
            return null;
        }
        String key = identity.source().toLowerCase(Locale.ROOT) + "\u0000" + identity.name().toLowerCase(Locale.ROOT);
        return new MonsterId("5etools:" + Base64.getUrlEncoder().withoutPadding().encodeToString(key.getBytes(StandardCharsets.UTF_8)));
    }

    private List<SourceReference> sourceRefs(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (!n.isArray()) {
            fail(p, "expected array");
        }
        List<SourceReference> out = new ArrayList<>();
        for (int i = 0; i < n.size(); i++) {
            JsonNode x = n.get(i);
            if (x.isTextual()) {
                out.add(new SourceReference(x.asText(), null));
            } else if (x.isObject()) {
                out.add(new SourceReference(textOrNull(x.get("source"), p), integer(x.get("page"), p)));
            } else {
                fail(p + "[" + i + "]", "expected string or object");
            }
        }
        return List.copyOf(out);
    }

    private CreatureType creatureType(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        String type = null;
        List<String> choices = List.of(), sideTags = List.of();
        List<TypeTag> tags = List.of();
        String swarm = null, sideType = null;
        Boolean hidden = null;
        if (n.isTextual()) {
            type = n.asText();
        } else {
            object(n, p);
            JsonNode t = n.get("type");
            if (t != null) {
                if (t.isTextual()) {
                    type = t.asText();
                } else if (t.isObject()) {
                    choices = strings(t.get("choose"), p + ".type.choose");
                } else {
                    fail(p + ".type", "unexpected creature type variant");
                }
            }
            tags = new ArrayList<>();
            JsonNode a = n.get("tags");
            if (a != null) {
                if (!a.isArray()) {
                    fail(p + ".tags", "expected array");
                }
                for (JsonNode x : a) {
                    tags.add(x.isTextual() ? new TypeTag(x.asText(), null, null) : new TypeTag(textOrNull(x.get("tag"), p), textOrNull(x.get("prefix"), p), bool(x.get("prefixHidden"), p)));
                }
            }
            swarm = textOrNull(n.get("swarmSize"), p);
            hidden = bool(n.get("sidekickHidden"), p);
            sideTags = strings(n.get("sidekickTags"), p);
            sideType = textOrNull(n.get("sidekickType"), p);
        }
        if (isEmpty(type) && choices.isEmpty() && tags.isEmpty() && isEmpty(swarm) && hidden == null
                && sideTags.isEmpty() && isEmpty(sideType)) {
            return null;
        }
        return new CreatureType(type, choices, List.copyOf(tags), swarm, hidden, sideTags, sideType);
    }

    private List<Alignment> alignments(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (!n.isArray()) {
            fail(p, "expected array");
        }
        List<Alignment> out = new ArrayList<>();
        for (JsonNode x : n) {
            if (x.isTextual()) {
                out.add(new Alignment(List.of(x.asText()), null, null, null));
            } else if (x.isObject()) {
                List<String> codes = new ArrayList<>();
                JsonNode a = x.get("alignment");
                if (a != null) {
                    codes.addAll(strings(a, p));
                }
                out.add(new Alignment(List.copyOf(codes), integer(x.get("chance"), p), textOrNull(x.get("special"), p), textOrNull(x.get("note"), p)));
            } else {
                fail(p, "unexpected alignment variant");
            }
        }
        return List.copyOf(out);
    }

    private ShortName shortName(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isBoolean()) {
            return new ShortName(n.asBoolean(), null);
        }
        if (n.isTextual()) {
            String value = n.asText();
            return isEmpty(value) ? null : new ShortName(null, value);
        }
        fail(p, "expected boolean or string");
        return null;
    }

    private Initiative initiative(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isIntegralNumber()) {
            return new Initiative(n.intValue(), null, null);
        }
        if (n.isObject()) {
            Integer value = integer(n.get("initiative"), p);
            Integer proficiency = integer(n.get("proficiency"), p);
            String advantage = textOrNull(n.get("advantage"), p);
            if (value == null && proficiency == null && isEmpty(advantage)) {
                return null;
            }
            return new Initiative(value, proficiency, advantage);
        }
        fail(p, "unsupported initiative value");
        return null;
    }

    private List<ArmorClass> armorClasses(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (!n.isArray()) {
            fail(p, "expected array");
        }
        List<ArmorClass> out = new ArrayList<>();
        for (JsonNode x : n) {
            if (x.isIntegralNumber()) {
                out.add(new ArmorClass(x.intValue(), List.of(), null, null, null));
            } else if (x.isObject()) {
                out.add(new ArmorClass(integer(x.get("ac"), p), strings(x.get("from"), p), textOrNull(x.get("condition"), p), bool(x.get("braces"), p), textOrNull(x.get("special"), p)));
            } else {
                fail(p, "unsupported AC item");
            }
        }
        return List.copyOf(out);
    }

    private HitPoints hitPoints(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isObject()) {
            fail(p, "expected object");
        }
        BigDecimal average = decimal(n.get("average"), p);
        String formula = textOrNull(n.get("formula"), p);
        String special = textOrNull(n.get("special"), p);
        if (average == null && isEmpty(formula) && isEmpty(special)) {
            return null;
        }
        return new HitPoints(average, formula, special);
    }

    private Movement movement(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        object(n, p);
        Map<String, MovementValue> modes = new TreeMap<>();
        List<AlternateMovement> alt = new ArrayList<>();
        for (Iterator<String> it = n.fieldNames(); it.hasNext(); ) {
            String k = it.next();
            JsonNode x = n.get(k);
            if (Set.of("canHover", "alternate", "choose", "note").contains(k)) {
                continue;
            }
            MovementValue v = movementValue(x, p + "." + k);
            modes.put(k, v);
        }
        JsonNode a = n.get("alternate");
        if (a != null) {
            if (a.isArray()) {
                for (JsonNode x : a) {
                    alt.add(new AlternateMovement(integer(x.get("number"), p), textOrNull(x.get("condition"), p), textOrNull(x.get("mode"), p)));
                }
            } else if (a.isObject()) {
                a.fields().forEachRemaining(e -> {
                    if (!e.getValue().isArray()) {
                        fail(p + ".alternate." + e.getKey(), "expected array");
                    }
                    for (JsonNode x : e.getValue()) {
                        alt.add(new AlternateMovement(integer(x.get("number"), p), textOrNull(x.get("condition"), p), e.getKey()));
                    }
                });
            } else {
                fail(p + ".alternate", "expected array or movement-mode object");
            }
        }
        JsonNode c = n.get("choose");
        Boolean canHover = bool(n.get("canHover"), p);
        Integer choiceAmount = integer(c == null ? null : c.get("amount"), p);
        List<String> choiceModes = strings(c == null ? null : c.get("from"), p);
        String choiceNote = textOrNull(c == null ? null : c.get("note"), p);
        String note = textOrNull(n.get("note"), p);
        if (modes.isEmpty() && canHover == null && alt.isEmpty() && choiceAmount == null
                && choiceModes.isEmpty() && isEmpty(choiceNote) && isEmpty(note)) {
            return null;
        }
        return new Movement(modes, canHover, List.copyOf(alt), choiceAmount, choiceModes, choiceNote, note);
    }

    private MovementValue movementValue(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isIntegralNumber()) {
            return new MovementValue(n.intValue(), null);
        }
        if (n.isObject()) {
            return new MovementValue(integer(n.get("number"), p), textOrNull(n.get("condition"), p));
        }
        fail(p, "unsupported movement mode");
        return null;
    }

    private ChallengeRating challenge(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isTextual() || n.isNumber()) {
            String value = n.asText();
            return isEmpty(value) ? null : new ChallengeRating(value, null, null, null, null);
        }
        if (n.isObject()) {
            String value = textOrNull(n.get("cr"), p);
            String covenValue = textOrNull(n.get("coven"), p);
            String lairValue = textOrNull(n.get("lair"), p);
            Integer experience = integer(n.get("xp"), p);
            Integer lairExperience = integer(n.get("lairXP"), p);
            if (isEmpty(value) && isEmpty(covenValue) && isEmpty(lairValue)
                    && experience == null && lairExperience == null) {
                return null;
            }
            return new ChallengeRating(value, covenValue, lairValue, experience, lairExperience);
        }
        fail(p, "unsupported challenge rating");
        return null;
    }

    private Map<String, String> stringMap(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return Map.of();
        }
        object(n, p);
        Map<String, String> m = new TreeMap<>();
        n.fields().forEachRemaining(e -> m.put(e.getKey(), scalarString(e.getValue(), p + "." + e.getKey())));
        return Collections.unmodifiableMap(m);
    }

    private Map<String, SkillValue> skills(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return Map.of();
        }
        object(n, p);
        Map<String, SkillValue> out = new TreeMap<>();
        n.fields().forEachRemaining(e -> {
            JsonNode v = e.getValue();
            if (v.isTextual() || v.isNumber()) {
                out.put(e.getKey(), new SkillBonus(v.asText()));
            } else if (v.isObject()) {
                List<Map<String, String>> choices = new ArrayList<>();
                JsonNode a = v.get("oneOf");
                if (a != null) {
                    if (a.isObject()) {
                        choices.add(stringMap(a, p + "." + e.getKey() + ".oneOf"));
                    } else if (a.isArray()) {
                        for (JsonNode opt : a) {
                            choices.add(stringMap(opt, p + "." + e.getKey() + ".oneOf"));
                        }
                    } else {
                        fail(p + "." + e.getKey() + ".oneOf", "expected object or array");
                    }
                }
                out.put(e.getKey(), new SkillAlternatives(List.copyOf(choices)));
            } else if (v.isArray()) {
                List<Map<String, String>> choices = new ArrayList<>();
                for (int i = 0; i < v.size(); i++) {
                    JsonNode opt = v.get(i);
                    JsonNode a = opt.isObject() ? opt.get("oneOf") : null;
                    if (a == null || !a.isObject()) {
                        fail(p + "." + e.getKey() + "[" + i + "]", "expected oneOf object");
                    }
                    choices.add(stringMap(a, p + "." + e.getKey() + "[" + i + "]"));
                }
                out.put(e.getKey(), new SkillAlternatives(List.copyOf(choices)));
            } else {
                fail(p + "." + e.getKey(), "unsupported skill value");
            }
        });
        return Collections.unmodifiableMap(out);
    }

    private PassivePerception passive(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isIntegralNumber()) {
            return new PassivePerception(n.intValue(), null);
        }
        if (n.isTextual()) {
            String value = n.asText();
            return isEmpty(value) ? null : new PassivePerception(null, value);
        }
        fail(p, "unsupported passive perception");
        return null;
    }

    private NullableList<String> nullableStrings(ObjectNode n, String key, String p) {
        if (!n.has(key)) {
            return null;
        }
        JsonNode v = n.get(key);
        return v.isNull() ? new NullableList<>(true, List.of()) : new NullableList<>(false, strings(v, p + "." + key));
    }

    private NullableList<Defense> defenses(ObjectNode n, String key, String p) {
        if (!n.has(key)) {
            return null;
        }
        JsonNode a = n.get(key);
        if (a.isNull()) {
            return new NullableList<>(true, List.of());
        }
        if (!a.isArray()) {
            fail(p + "." + key, "expected array or null");
        }
        List<Defense> out = new ArrayList<>();
        for (int i = 0; i < a.size(); i++) {
            out.add(defense(a.get(i), p + "." + key + "[" + i + "]"));
        }
        return new NullableList<>(false, List.copyOf(out));
    }

    private Defense defense(JsonNode n, String p) {
        if (n.isTextual()) {
            return new Defense(List.of(n.asText()), null, null, null, null, null, List.of());
        }
        if (!n.isObject()) {
            fail(p, "expected string or defense object");
        }
        List<String> terms = new ArrayList<>();
        List<Defense> nested = new ArrayList<>();
        for (String k : List.of("resist", "immune", "vulnerable", "conditionImmune")) {
            JsonNode v = n.get(k);
            if (v != null) {
                if (!v.isArray()) {
                    fail(p + "." + k, "expected array");
                }
                for (int i = 0; i < v.size(); i++) {
                    JsonNode child = v.get(i);
                    if (child.isTextual()) {
                        terms.add(child.asText());
                    } else {
                        nested.add(defense(child, p + "." + k + "[" + i + "]"));
                    }
                }
            }
        }
        JsonNode a = n.get("note");
        if (a != null && a.isArray()) {
            for (int i = 0; i < a.size(); i++) {
                nested.add(defense(a.get(i), p + ".note[" + i + "]"));
            }
        }
        return new Defense(List.copyOf(terms), bool(n.get("cond"), p), a != null && a.isTextual() ? a.asText() : null, textOrNull(n.get("preNote"), p), textOrNull(n.get("postNote"), p), textOrNull(n.get("special"), p), List.copyOf(nested));
    }

    private List<Feature> features(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (!n.isArray()) {
            fail(p, "expected feature array");
        }
        List<Feature> out = new ArrayList<>();
        for (int i = 0; i < n.size(); i++) {
            JsonNode x = n.get(i);
            String q = p + "[" + i + "]";
            if (!x.isObject()) {
                fail(q, "expected feature object");
            }
            JsonNode entries = x.get("entries");
            List<MonsterContentDto> content = contentList(entries, q + ".entries");
            JsonNode version = x.get("_version");
            VariantReference variant = null;
            if (version != null) {
                object(version, q + "._version");
                variant = new VariantReference(textOrNull(version.get("name"), q), textOrNull(version.get("addAs"), q), textOrNull(version.get("addHeadersAs"), q));
            }
            out.add(new Feature(textOrNull(x.get("name"), q), content, textOrNull(x.get("type"), q), textOrNull(x.get("source"), q), integer(x.get("page"), q), reference(x.get("token"), q + ".token"), variant));
        }
        return List.copyOf(out);
    }

    private List<SpellcastingBlock> spellcasting(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (!n.isArray()) {
            fail(p, "expected array");
        }
        List<SpellcastingBlock> out = new ArrayList<>();
        for (int i = 0; i < n.size(); i++) {
            out.add(spellBlock(n.get(i), p + "[" + i + "]"));
        }
        return List.copyOf(out);
    }

    private SpellcastingBlock spellBlock(JsonNode n, String p) {
        object(n, p);
        Set<String> fixed = Set.of("name", "type", "ability", "displayAs", "headerEntries", "footerEntries", "will", "spells", "constant", "ritual", "hidden", "chargesItem", "daily", "rest", "restLong", "recharge", "legendary", "charges", "yearly", "weekly", "monthly", "save", "attack", "level", "slots", "lower");
        Map<String, SpellLevel> levels = new TreeMap<>();
        JsonNode s = n.get("spells");
        if (s != null && s.isObject()) {
            s.fields().forEachRemaining(e -> {
                JsonNode v = e.getValue();
                levels.put(e.getKey(), new SpellLevel(integer(v.get("slots"), p), integer(v.get("lower"), p), spellRefs(v.get("spells"), p)));
            });
        }
        Map<String, Map<String, List<SpellReference>>> usage = new TreeMap<>();
        for (String bucket : List.of("daily", "rest", "restLong", "recharge", "legendary", "charges", "yearly", "weekly", "monthly")) {
            JsonNode v = n.get(bucket);
            if (v != null) {
                Map<String, List<SpellReference>> buckets = new TreeMap<>();
                if (v.isObject()) {
                    v.fields().forEachRemaining(e -> buckets.put(e.getKey(), spellRefs(e.getValue(), p)));
                } else if (v.isArray()) {
                    buckets.put("", spellRefs(v, p));
                } else {
                    fail(p + "." + bucket, "expected object or array");
                }
                usage.put(bucket, Collections.unmodifiableMap(buckets));
            }
        }
        return new SpellcastingBlock(textOrNull(n.get("name"), p), textOrNull(n.get("type"), p), textOrNull(n.get("ability"), p), textOrNull(n.get("displayAs"), p), contentList(n.get("headerEntries"), p), contentList(n.get("footerEntries"), p), spellRefs(n.get("will"), p), levels, usage, spellRefs(n.get("constant"), p), spellRefs(n.get("ritual"), p), set(n.get("hidden"), p), textOrNull(n.get("chargesItem"), p), integer(n.get("save"), p), integer(n.get("attack"), p), integer(n.get("level"), p), integerMap(n.get("slots"), p), extras(n, fixed));
    }

    private Map<String, Integer> integerMap(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return Map.of();
        }
        object(n, p);
        Map<String, Integer> m = new TreeMap<>();
        n.fields().forEachRemaining(e -> m.put(e.getKey(), integer(e.getValue(), p)));
        return Collections.unmodifiableMap(m);
    }

    private List<SpellReference> spellRefs(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        List<SpellReference> out = new ArrayList<>();
        if (n.isArray()) {
            for (JsonNode x : n) {
                out.add(spellRef(x, p));
            }
        } else if (n.isObject()) {
            out.add(spellRef(n, p));
        } else if (n.isTextual()) {
            out.add(new SpellReference(n.asText(), null, null));
        } else {
            fail(p, "invalid spell reference list");
        }
        return List.copyOf(out);
    }

    private SpellReference spellRef(JsonNode n, String p) {
        if (n.isTextual()) {
            return new SpellReference(n.asText(), null, null);
        }
        if (n.isObject()) {
            JsonNode text = n.has("entry") ? n.get("entry") : n.get("spell");
            return new SpellReference(textOrNull(text, p), bool(n.get("hidden"), p), null);
        }
        fail(p, "invalid spell reference");
        return null;
    }

    private List<MonsterContentDto> contentList(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (!n.isArray()) {
            return List.of(content(n, p));
        }
        List<MonsterContentDto> out = new ArrayList<>();
        for (int i = 0; i < n.size(); i++) {
            out.add(content(n.get(i), p + "[" + i + "]"));
        }
        return List.copyOf(out);
    }

    private MonsterContentDto content(JsonNode n, String p) {
        if (n.isTextual()) {
            return new PlainText(n.asText());
        }
        if (n.isNumber() || n.isBoolean()) {
            return new PlainText(n.asText());
        }
        if (!n.isObject()) {
            fail(p, "unsupported content node");
        }
        String type = textOrNull(n.get("type"), p);
        String name = textOrNull(n.get("name"), p);
        String entry = textOrNull(n.get("entry"), p);
        List<MonsterContentDto> entries = contentList(n.get("entries"), p + ".entries");
        if ("list".equals(type)) {
            return new ListContent(textOrNull(n.get("style"), p), contentList(n.get("items"), p + ".items"), extras(n, Set.of("type", "style", "items")));
        }
        if ("item".equals(type)) {
            return new ListItem(name, entry, entries, extras(n, Set.of("type", "name", "entry", "entries")));
        }
        if ("table".equals(type)) {
            List<List<TableCellDto>> rows = new ArrayList<>();
            JsonNode r = n.get("rows");
            if (r != null) {
                if (!r.isArray()) {
                    fail(p + ".rows", "expected array");
                }
                for (JsonNode row : r) {
                    if (!row.isArray()) {
                        fail(p + ".rows", "expected row array");
                    }
                    List<TableCellDto> cells = new ArrayList<>();
                    for (JsonNode c : row) {
                        cells.add(cell(c, p + ".rows"));
                    }
                    rows.add(List.copyOf(cells));
                }
            }
            return new TableContent(textOrNull(n.get("caption"), p), strings(n.get("colLabels"), p), strings(n.get("colStyles"), p), List.copyOf(rows), textOrNull(n.get("source"), p), integer(n.get("page"), p), extras(n, Set.of("type", "caption", "colLabels", "colStyles", "rows", "source", "page")));
        }
        if ("quote".equals(type)) {
            return new QuoteContent(textOrNull(n.get("by"), p), entries, extras(n, Set.of("type", "by", "entries")));
        }
        if ("inset".equals(type) || "insetReadaloud".equals(type)) {
            return new InsetContent(name, entries, textOrNull(n.get("source"), p), integer(n.get("page"), p), extras(n, Set.of("type", "name", "entries", "source", "page")));
        }
        if ("spellcasting".equals(type) || n.has("spells")) {
            return new SpellcastingContent(spellBlock(n, p));
        }
        return new Section(type, name, entry, entries, textOrNull(n.get("source"), p), integer(n.get("page"), p), extras(n, Set.of("type", "name", "entry", "entries", "source", "page")));
    }

    private TableCellDto cell(JsonNode n, String p) {
        if (n.isTextual() || n.isNumber()) {
            return new TextCell(n.asText());
        }
        if (n.isObject()) {
            if (n.has("roll")) {
                JsonNode r = n.get("roll");
                if (r.isIntegralNumber()) {
                    return new RollCell(r.intValue(), null, null, null);
                }
                if (r.isObject()) {
                    return new RollCell(integer(r.get("exact"), p), integer(r.get("min"), p), integer(r.get("max"), p), bool(r.get("pad"), p));
                }
            }
            return new StructuredCell(textOrNull(n.get("type"), p), textOrNull(n.get("name"), p), contentList(n.get("entries"), p), extras(n, Set.of("type", "name", "entries")));
        }
        fail(p, "unsupported table cell");
        return null;
    }

    private Map<String, ContentValue> extras(JsonNode n, Set<String> known) {
        Map<String, ContentValue> out = new TreeMap<>();
        n.fields().forEachRemaining(e -> {
            if (!known.contains(e.getKey())) {
                out.put(e.getKey(), contentValue(e.getValue()));
            }
        });
        return Collections.unmodifiableMap(out);
    }

    private ContentValue contentValue(JsonNode n) {
        if (n == null || n.isNull()) {
            return new ContentNull();
        }
        if (n.isTextual()) {
            return new ContentText(n.asText());
        }
        if (n.isNumber()) {
            return new ContentNumber(n.decimalValue());
        }
        if (n.isBoolean()) {
            return new ContentBoolean(n.booleanValue());
        }
        if (n.isArray()) {
            List<ContentValue> a = new ArrayList<>();
            n.forEach(x -> a.add(contentValue(x)));
            return new ContentArray(List.copyOf(a));
        }
        Map<String, ContentValue> o = new TreeMap<>();
        n.fields().forEachRemaining(e -> o.put(e.getKey(), contentValue(e.getValue())));
        return new ContentObject(Collections.unmodifiableMap(o));
    }

    private List<GearReference> gear(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        List<GearReference> out = new ArrayList<>();
        if (!n.isArray()) {
            fail(p + ".gear", "expected array");
        }
        for (JsonNode x : n) {
            if (x.isTextual()) {
                out.add(new GearReference(x.asText(), null, null));
            } else if (x.isObject()) {
                out.add(new GearReference(textOrNull(x.get("item"), p), textOrNull(x.get("displayName"), p), integer(x.get("quantity"), p)));
            } else {
                fail(p + ".gear", "unsupported gear value");
            }
        }
        return List.copyOf(out);
    }

    private Reference reference(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (n.isObject()) {
            String name = textOrNull(n.get("name"), p);
            String source = textOrNull(n.get("source"), p);
            if (isEmpty(name) && isEmpty(source)) {
                return null;
            }
            return new Reference(name, source);
        }
        if (n.isTextual()) {
            String name = n.asText();
            return isEmpty(name) ? null : new Reference(name, null);
        }
        fail(p, "expected reference object or string");
        return null;
    }

    private List<String> strings(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return List.of();
        }
        if (n.isTextual()) {
            return List.of(n.asText());
        }
        if (!n.isArray()) {
            fail(p, "expected string array");
        }
        List<String> out = new ArrayList<>();
        for (JsonNode x : n) {
            if (!x.isTextual()) {
                fail(p, "expected string array member");
            }
            out.add(x.asText());
        }
        return List.copyOf(out);
    }

    private Set<String> set(JsonNode n, String p) {
        return Collections.unmodifiableSet(new TreeSet<>(strings(n, p)));
    }

    private String scalarString(JsonNode n, String p) {
        if (n.isTextual() || n.isNumber()) {
            return n.asText();
        }
        fail(p, "expected scalar string/number");
        return null;
    }

    private String text(JsonNode n, String p) {
        if (n.isTextual()) {
            return n.asText();
        }
        fail(p, "expected string");
        return null;
    }

    private String textOrNull(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        return text(n, p);
    }

    private Integer integer(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isIntegralNumber() || !n.canConvertToInt()) {
            fail(p, "expected 32-bit integer");
        }
        return n.intValue();
    }

    private BigDecimal decimal(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isNumber()) {
            fail(p, "expected numeric value");
        }
        return n.decimalValue();
    }

    private Boolean bool(JsonNode n, String p) {
        if (n == null || n.isNull()) {
            return null;
        }
        if (!n.isBoolean()) {
            fail(p, "expected boolean");
        }
        return n.booleanValue();
    }

    private JsonNode req(ObjectNode n, String k, String p) {
        JsonNode v = n.get(k);
        if (v == null || v.isNull()) {
            fail(p + "." + k, "required property is missing or null");
        }
        return v;
    }

    private void requireObject(JsonNode n, String p) {
        if (!n.isObject()) {
            fail(p, "expected object");
        }
    }

    private void object(JsonNode n, String p) {
        if (!n.isObject()) {
            fail(p, "expected object");
        }
    }

    private void fail(String p, String why) {
        throw new IllegalArgumentException("Monster mapping error at " + p + ": " + why);
    }
}

