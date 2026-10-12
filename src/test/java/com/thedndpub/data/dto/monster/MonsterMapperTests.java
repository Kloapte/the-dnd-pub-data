package com.thedndpub.data.dto.monster;

import com.thedndpub.data.dto.monster.content.InsetContent;
import com.thedndpub.data.dto.monster.content.ListContent;
import com.thedndpub.data.dto.monster.content.MonsterContentDto;
import com.thedndpub.data.dto.monster.content.PlainText;
import com.thedndpub.data.dto.monster.content.QuoteContent;
import com.thedndpub.data.dto.monster.content.RollCell;
import com.thedndpub.data.dto.monster.content.Section;
import com.thedndpub.data.dto.monster.content.SpellcastingContent;
import com.thedndpub.data.dto.monster.content.StructuredCell;
import com.thedndpub.data.dto.monster.content.TableContent;
import com.thedndpub.data.dto.monster.content.TextCell;
import com.thedndpub.data.dto.monster.identity.MonsterId;
import com.thedndpub.data.dto.monster.identity.Source;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.monster.*;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MonsterMapperTests {
    private final ObjectMapper json = new ObjectMapper();

    @Test
    void mapsCompleteExpandedCatalogDeterministicallyAndRetainsLineage() throws Exception {
        List<MonsterSourceRecord> input = loadCorpus();
        assertEquals(4560, input.size());
        assertEquals(111, monsterFileCount());
        MonsterCopyResolver resolver = new DefaultMonsterCopyResolver(json, MonsterTemplateRepository.fromResource(json));
        List<ResolvedMonster> originals = resolver.resolveAll(input);
        assertEquals(4560, originals.size());
        List<ResolvedMonster> resolved = new MonsterVersionExpander(json, resolver).expandAll(originals);
        assertEquals(4851, resolved.size());
        assertEquals(291, resolved.stream().filter(x -> x.provenance().generatedVersion() != null).count());
        assertEquals(85, resolved.stream().filter(x -> x.provenance().generatedVersion() == null && !x.provenance().sourceVersionDefinitions().isEmpty()).count());
        String before = json.writeValueAsString(resolved.stream().map(ResolvedMonster::content).toList());
        MonsterMapper mapper = new MonsterMapper(json);
        List<NormalizedMonsterRecord> mapped = mapper.mapRecords(resolved);
        assertEquals(4851, mapped.size());
        assertEquals(4851, mapped.stream().map(x -> x.monster().identity().id()).distinct().count());
        assertEquals(4851, mapped.stream().filter(x -> x.classification() != null).count());
        assertTrue(mapped.stream().anyMatch(x -> !x.classification().actionTags().isEmpty()
                || !x.classification().damageTags().isEmpty()
                || !x.classification().traitTags().isEmpty()));
        assertEquals(mapped.size(), mapper.mapRecords(resolved.reversed()).size());
        Map<String, String> first = new HashMap<>();
        for (NormalizedMonsterRecord item : mapped) {
            first.put(item.monster().identity().id().value(), json.writeValueAsString(item));
        }
        for (NormalizedMonsterRecord item : mapper.mapRecords(resolved.reversed())) {
            assertEquals(first.get(item.monster().identity().id().value()), json.writeValueAsString(item));
        }
        assertEquals(291, mapped.stream().filter(x -> x.provenance().versionParentId() != null).count());
        assertTrue(mapped.stream().allMatch(x -> x.provenance().sourceFile() != null));
        assertEquals(before, json.writeValueAsString(resolved.stream().map(ResolvedMonster::content).toList()), "mapping must not mutate resolved content");
        assertTrue(mapped.stream().map(NormalizedMonsterRecord::monster).noneMatch(this::containsResolverInstructions));

        var cat = find(mapped, "Cat Skeleton", "CoS").monster();
        assertTrue(cat.defenses().vulnerabilities().explicitNull());
        assertTrue(cat.statistics().languages().explicitNull());
        var familiar = mapped.stream().filter(x -> x.provenance().versionParentId() != null && x.monster().identity().name().startsWith("Battle Familiar")).toList();
        assertEquals(3, familiar.size());
        var draconicParent = new MonsterId("5etools:" + Base64.getUrlEncoder().withoutPadding()
                .encodeToString("xphb\u0000draconic spirit".getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        var spirit = mapped.stream().filter(x -> draconicParent.equals(x.provenance().versionParentId())).toList();
        assertEquals(5, spirit.size());
        assertTrue(spirit.stream().anyMatch(x -> json.valueToTree(x.monster()).toString().contains("{@damage 2d6}")));
        assertTrue(mapped.stream().filter(x -> x.monster().identity().name().equals("Skaab: Armor Plating")).findAny().isPresent());
        assertTrue(mapped.stream().map(x -> json.valueToTree(x.monster()).toString()).anyMatch(s -> s.contains("yearly")));
        var zikran = find(mapped, "Zikran", "CM").monster();
        assertFalse(zikran.spellcasting().isEmpty());
        assertTrue(zikran.spellcasting().stream().anyMatch(b -> b.spellLevels().containsKey("1")));
        assertTrue(mapped.stream().anyMatch(x -> x.monster().defenses() != null
                && x.monster().defenses().resistances() != null
                && !x.monster().defenses().resistances().values().isEmpty()
                && x.monster().defenses().resistances().values().stream().anyMatch(d -> !d.nested().isEmpty())));
        ResolvedMonster absent = fixture("Absent", "TST", "{\"name\":\"Absent\",\"source\":\"TST\"}");
        assertNull(mapper.map(absent).statistics().languages());
    }

    @Test
    void approvedInventoryAccountsForEveryObservedPropertyPathAndRoot() throws Exception {
        JsonNode inventory = json.readTree(Path.of("analysis/monsters/normalized-field-inventory.json").toFile());
        assertEquals(4851, inventory.path("expandedCount").asInt());
        assertEquals(535, inventory.path("resolvedProperties").asInt());
        Set<String> roots = new TreeSet<>();
        for (JsonNode field : inventory.path("properties")) {
            roots.add(field.path("rootProperty").asText());
            String root = field.path("rootProperty").asText();
            String expectedStatus = "MAPPED";
            if (classificationRoots().contains(root)) {
                expectedStatus = "INTERNAL_METADATA";
            } else if (excludedPresentationRoots().contains(root)) {
                expectedStatus = "EXCLUDED_PUBLIC_METADATA";
            }
            assertEquals(expectedStatus, field.path("coverageStatus").asText(),
                    field.path("path").asText() + " has an unexpected coverage status");
            assertFalse(field.path("normalizedDestination").asText().isBlank(), field.path("path").asText());
        }
        assertEquals(92, roots.size());
        assertEquals(roots, MonsterMapper.mappedRootProperties());
    }

    private Set<String> classificationRoots() {
        return Set.of("actionTags", "damageTags", "damageTagsLegendary", "damageTagsSpell",
                "conditionInflict", "conditionInflictLegendary", "conditionInflictSpell", "languageTags",
                "miscTags", "senseTags", "spellcastingTags", "traitTags");
    }

    private Set<String> excludedPresentationRoots() {
        return Set.of("hasFluff", "hasFluffImages", "hasToken", "tokenCustom", "tokenCredit",
                "foundryTokenScale", "altArt", "soundClip");
    }

    @Test
    void failsWithPathForUnsupportedRootAndMalformedEntry() throws Exception {
        ResolvedMonster malformed = fixture("Probe", "TST", "{\"name\":\"Probe\",\"source\":\"TST\",\"unknownThing\":1}");
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new MonsterMapper(json).map(malformed));
        assertTrue(e.getMessage().contains("$.unknownThing"));
    }

    @Test
    void summaryProjectsNormalizedFields() throws Exception {
        ResolvedMonster resolved = fixture("Probe", "TST", "{\"name\":\"Probe\",\"source\":\"TST\",\"cr\":\"1/2\",\"ac\":[12]}");
        var dto = new MonsterMapper(json).map(resolved);
        var summary = MonsterSummaryDto.from(dto);
        assertEquals(dto.identity().id(), summary.id());
        assertEquals("1/2", summary.challengeRating().value());
        assertEquals(12, summary.representativeArmorClass().value());
    }

    @Test
    void mapsPolymorphicStatisticsAndRecursiveContentWithoutFlattening() throws Exception {
        ResolvedMonster source = fixture("Typed", "TST", """
                {"name":"Typed","source":"TST","type":{"type":{"choose":["fey","fiend"]},"tags":[{"tag":"shapechanger","prefix":"any"}]},
                 "alignment":[{"alignment":["N","G"],"chance":50},"U"],"ac":[12,{"ac":15,"from":["natural armor"],"condition":"while transformed"},{"special":"as armor"}],
                 "hp":{"average":5.5,"formula":"1d8 + 1"},"speed":{"walk":{"number":30,"condition":"in armor"},"fly":60,"canHover":true,"alternate":{"walk":[{"number":20,"condition":"slowed"}]}},
                 "cr":{"cr":"1/2","xp":100},"resist":[{"resist":["cold",{"resist":["fire"],"note":"while raging"}],"cond":true,"preNote":"nonmagical"}],
                 "action":[{"name":"Multi-form","entries":["Text {@damage 1d6}.",{"type":"entries","name":"Section","entry":"Lead","entries":[{"type":"list","style":"list-hang","items":[{"type":"item","name":"Choice","entry":"Detail"}]}]},
                    {"type":"table","colLabels":["d4","Result"],"rows":[["1",{"type":"cell","roll":{"min":2,"max":4,"pad":true}},{"type":"entries","name":"Cell","entries":["inside"]}]]},
                    {"type":"quote","by":"Source","entries":["quoted"]},{"type":"inset","name":"Inset","entries":["inset text"]},
                    {"type":"spellcasting","name":"Caster","ability":"int","headerEntries":["Header"],"spells":{"1":{"slots":2,"spells":["{@spell magic missile}"]}}}]}]}
                """);
        MonsterDto dto = new MonsterMapper(json).map(source);
        assertEquals(List.of("fey", "fiend"), dto.identity().creatureType().choices());
        assertEquals(50, dto.identity().alignments().getFirst().chance());
        assertEquals(3, dto.statistics().armorClasses().size());
        assertEquals("1/2", dto.statistics().challengeRating().value());
        assertEquals(new java.math.BigDecimal("5.5"), dto.statistics().hitPoints().average());
        assertEquals("in armor", dto.statistics().movement().modes().get("walk").condition());
        assertEquals("slowed", dto.statistics().movement().alternate().getFirst().condition());
        assertEquals("fire", dto.defenses().resistances().values().getFirst().nested().getFirst().terms().getFirst());
        List<MonsterContentDto> content = dto.features().actions().getFirst().entries();
        assertInstanceOf(PlainText.class, content.getFirst());
        Section section = assertInstanceOf(Section.class, content.get(1));
        assertInstanceOf(ListContent.class, section.entries().getFirst());
        assertInstanceOf(TableContent.class, content.get(2));
        TableContent table = (TableContent) content.get(2);
        assertInstanceOf(TextCell.class, table.rows().getFirst().getFirst());
        RollCell roll = assertInstanceOf(RollCell.class, table.rows().getFirst().get(1));
        assertEquals(2, roll.minimum());
        assertEquals(4, roll.maximum());
        assertTrue(roll.pad());
        assertInstanceOf(StructuredCell.class, table.rows().getFirst().get(2));
        assertInstanceOf(QuoteContent.class, content.get(3));
        assertInstanceOf(InsetContent.class, content.get(4));
        SpellcastingContent spellcasting = assertInstanceOf(SpellcastingContent.class, content.get(5));
        assertEquals("{@spell magic missile}", spellcasting.spellcasting().spellLevels().get("1").spells().getFirst().sourceText());
    }

    private boolean containsResolverInstructions(MonsterDto dto) {
        JsonNode n = json.valueToTree(dto);
        for (String key : List.of("_copy", "_versions", "_mod", "_templates")) {
            if (n.toString().contains("\"" + key + "\"")) {
                return true;
            }
        }
        return false;
    }

    private NormalizedMonsterRecord find(List<NormalizedMonsterRecord> all, String name, String source) {
        return all.stream().filter(x -> x.monster().identity().name().equalsIgnoreCase(name) && x.monster().identity().source().code().equalsIgnoreCase(source)).findFirst().orElseThrow();
    }

    private List<MonsterSourceRecord> loadCorpus() throws Exception {
        List<MonsterSourceRecord> records = new ArrayList<>();
        Path directory = Path.of("src/main/resources/data/monsters");
        try (var files = Files.list(directory)) {
            for (Path file : files.filter(p -> p.getFileName().toString().startsWith("bestiary-") && p.toString().endsWith(".json")).sorted().toList()) {
                JsonNode monsters = json.readTree(file.toFile()).path("monster");
                for (int i = 0; i < monsters.size(); i++) {
                    var raw = (com.fasterxml.jackson.databind.node.ObjectNode) monsters.get(i);
                    var id = MonsterIdentity.from(raw.path("name").asText(), raw.path("source").asText());
                    records.add(new MonsterSourceRecord(id, new MonsterSourceLocation(file.getFileName().toString(), i), json.treeToValue(raw, MonsterDte.class), raw));
                }
            }
        }
        return records;
    }

    private long monsterFileCount() throws Exception {
        try (var files = Files.list(Path.of("src/main/resources/data/monsters"))) {
            return files.filter(p -> p.getFileName().toString().startsWith("bestiary-") && p.toString().endsWith(".json")).count();
        }
    }

    private ResolvedMonster fixture(String name, String source, String raw) throws Exception {
        var node = (com.fasterxml.jackson.databind.node.ObjectNode) json.readTree(raw);
        return new ResolvedMonster(MonsterIdentity.from(name, source), new MonsterSourceLocation("fixture.json", 0), null, List.of(), node,
                new ResolutionProvenance("test", "test", null, null, false, Set.of()));
    }
}


