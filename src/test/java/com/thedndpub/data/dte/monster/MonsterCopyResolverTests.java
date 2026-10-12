package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.nio.file.*;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MonsterCopyResolverTests {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void preservesCopyGraphFailuresAndDoesNotMutateInputs() {
        ObjectNode base = mapper.createObjectNode().put("name", "Base").put("source", "TST").put("str", 12);
        ObjectNode child = mapper.createObjectNode().put("name", "Child").put("source", "TST");
        child.putObject("_copy").put("name", "Absent").put("source", "TST");
        MonsterDte baseDte = new MonsterDte(null, null, null, null, null, null, null, null);
        MonsterDte childDte = mapper.convertValue(child, MonsterDte.class);
        MonsterSourceRecord a = new MonsterSourceRecord(MonsterIdentity.from("Base", "TST"), new MonsterSourceLocation("a.json", 0), baseDte, base);
        MonsterSourceRecord b = new MonsterSourceRecord(MonsterIdentity.from("Child", "TST"), new MonsterSourceLocation("a.json", 1), childDte, child);
        var error = assertThrows(MonsterResolutionException.class, () -> new MonsterCopyGraph(List.of(a, b)));
        assertTrue(error.getMessage().toLowerCase(Locale.ROOT).contains("absent"));
        assertFalse(child.has("str"));
        assertTrue(child.has("_copy"));
    }

    @Test
    void resolvesShallowInheritanceAndDistinguishesNullFromAbsent() throws Exception {
        ObjectNode parent = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","hp":{"average":20},"speed":{"walk":30,"fly":60},"languages":["Common"]}
                """);
        ObjectNode child = (ObjectNode) mapper.readTree("""
                {"name":"Child","source":"TST","languages":null,"speed":{"walk":20},"_copy":{"name":"Base","source":"TST"}}
                """);
        var a = new MonsterSourceRecord(MonsterIdentity.from("Base", "TST"), new MonsterSourceLocation("fixture.json", 0), new MonsterDte(null, null, null, null, null, null, null, null), parent);
        var b = new MonsterSourceRecord(MonsterIdentity.from("Child", "TST"), new MonsterSourceLocation("fixture.json", 1), mapper.treeToValue(child, MonsterDte.class), child);
        JsonNode result = new DefaultMonsterCopyResolver(mapper, MonsterTemplateRepository.fromResource(mapper)).resolveAll(List.of(a, b)).get(1).content();
        assertEquals(20, result.path("hp").path("average").asInt());
        assertEquals(20, result.path("speed").path("walk").asInt());
        assertFalse(result.path("speed").has("fly"));
        assertFalse(result.has("languages"));
    }

    @Test
    void appliesChildModifierBeforeTemplateModifierAndProtectsInlineTags() throws Exception {
        ObjectNode parent = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","trait":[],"action":[{"name":"Text","entries":["a mage knows {@spell mage hand}"]}]}
                """);
        ObjectNode child = (ObjectNode) mapper.readTree("""
                {"name":"Child","source":"TST","_copy":{"name":"Base","source":"TST","_templates":[{"name":"Deep Gnome","source":"DMG"}],"_mod":{"trait":{"mode":"appendArr","items":{"name":"Child First","entries":["before template"]}},"*":{"mode":"replaceTxt","replace":"mage","with":"wizard"}}}}
                """);
        var a = new MonsterSourceRecord(MonsterIdentity.from("Base", "TST"), new MonsterSourceLocation("fixture.json", 0), new MonsterDte(null, null, null, null, null, null, null, null), parent);
        var b = new MonsterSourceRecord(MonsterIdentity.from("Child", "TST"), new MonsterSourceLocation("fixture.json", 1), mapper.treeToValue(child, MonsterDte.class), child);
        JsonNode result = new DefaultMonsterCopyResolver(mapper, MonsterTemplateRepository.fromResource(mapper)).resolveAll(List.of(a, b)).get(1).content();
        assertEquals("Child First", result.path("trait").get(0).path("name").asText());
        assertTrue(result.path("action").get(0).path("entries").get(0).asText().contains("a wizard knows {@spell mage hand}"));
    }

    @Test
    void rejectsTemplateCyclesAndDuplicateMonsterIdentities() throws Exception {
        String cyclic = "{\"monsterTemplate\":[{\"name\":\"A\",\"source\":\"T\",\"_copy\":{\"name\":\"B\",\"source\":\"T\"}},{\"name\":\"B\",\"source\":\"T\",\"_copy\":{\"name\":\"A\",\"source\":\"T\"}}]}";
        var cycle = assertThrows(MonsterResolutionException.class, () -> new MonsterTemplateRepository(mapper, new ByteArrayInputStream(cyclic.getBytes(StandardCharsets.UTF_8))));
        assertTrue(cycle.getMessage().toLowerCase(Locale.ROOT).contains("cycle"));
        String missing = "{\"monsterTemplate\":[{\"name\":\"A\",\"source\":\"T\",\"_copy\":{\"name\":\"Absent\",\"source\":\"T\"}}]}";
        var missingError = assertThrows(MonsterResolutionException.class, () -> new MonsterTemplateRepository(mapper, new ByteArrayInputStream(missing.getBytes(StandardCharsets.UTF_8))));
        assertTrue(missingError.getMessage().toLowerCase(Locale.ROOT).contains("missing"));
        ObjectNode raw = mapper.createObjectNode().put("name", "Same").put("source", "T");
        var dte = new MonsterDte(null, null, null, null, null, null, null, null);
        var one = new MonsterSourceRecord(MonsterIdentity.from("Same", "T"), new MonsterSourceLocation("one.json", 0), dte, raw);
        var two = new MonsterSourceRecord(MonsterIdentity.from("Same", "T"), new MonsterSourceLocation("two.json", 0), dte, raw);
        assertThrows(MonsterResolutionException.class, () -> new MonsterCopyGraph(List.of(one, two)));
        var cycleA = mapper.createObjectNode().put("name", "A").put("source", "T");
        var cycleB = mapper.createObjectNode().put("name", "B").put("source", "T");
        var ca = new MonsterSourceRecord(MonsterIdentity.from("A", "T"), new MonsterSourceLocation("cycle.json", 0), new MonsterDte(null, null, null, null, null, null, new MonsterCopyReferenceDte("B", "T", null, null, null), null), cycleA);
        var cb = new MonsterSourceRecord(MonsterIdentity.from("B", "T"), new MonsterSourceLocation("cycle.json", 1), new MonsterDte(null, null, null, null, null, null, new MonsterCopyReferenceDte("A", "T", null, null, null), null), cycleB);
        assertTrue(assertThrows(MonsterResolutionException.class, () -> new MonsterCopyGraph(List.of(ca, cb))).getMessage().toLowerCase(Locale.ROOT).contains("cycle"));
    }

    @Test
    void supportsRenameArrayModeWithoutExpandingVersions() throws Exception {
        ObjectNode parent = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","action":[{"name":"Old","entries":["Keep"]}]}
                """);
        ObjectNode child = (ObjectNode) mapper.readTree("""
                {"name":"Child","source":"TST","_copy":{"name":"Base","source":"TST","_mod":{"action":{"mode":"renameArr","renames":{"rename":"Old","with":"New"}}}}}
                """);
        var base = new MonsterSourceRecord(MonsterIdentity.from("Base", "TST"), new MonsterSourceLocation("fixture.json", 0), new MonsterDte(null, null, null, null, null, null, null, null), parent);
        var childDte = new MonsterDte(null, null, null, null, null, null, new MonsterCopyReferenceDte("Base", "TST", null, null, null), null);
        var copied = new MonsterSourceRecord(MonsterIdentity.from("Child", "TST"), new MonsterSourceLocation("fixture.json", 1), childDte, child);
        var resolved = new DefaultMonsterCopyResolver(mapper, MonsterTemplateRepository.fromResource(mapper)).resolveAll(List.of(base, copied));
        assertEquals("New", resolved.get(1).content().path("action").get(0).path("name").asText());
        assertTrue(resolved.get(1).provenance().appliedModifierModes().contains("renameArr"));
    }

    @Test
    void preservesPinnedRemoveSpellsBehaviorForWillConstantAndRitual() throws Exception {
        ObjectNode parent = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","spellcasting":[{"name":"Magic","will":["{@spell light}"],"constant":["{@spell detect magic}"],"ritual":["{@spell alarm}"],"daily":{"1e":["{@spell shield}"]}}]}
                """);
        ObjectNode child = (ObjectNode) mapper.readTree("""
                {"name":"Child","source":"TST","_copy":{"name":"Base","source":"TST","_mod":{"_":{"mode":"removeSpells","will":["{@spell light}"],"constant":["{@spell detect magic}"],"ritual":["{@spell alarm}"],"daily":{"1e":["{@spell shield}"]}}}}}
                """);
        var base = new MonsterSourceRecord(MonsterIdentity.from("Base", "TST"), new MonsterSourceLocation("fixture.json", 0), new MonsterDte(null, null, null, null, null, null, null, null), parent);
        var childDte = new MonsterDte(null, null, null, null, null, null, new MonsterCopyReferenceDte("Base", "TST", null, null, null), null);
        var copied = new MonsterSourceRecord(MonsterIdentity.from("Child", "TST"), new MonsterSourceLocation("fixture.json", 1), childDte, child);
        JsonNode content = new DefaultMonsterCopyResolver(mapper, MonsterTemplateRepository.fromResource(mapper)).resolveAll(List.of(base, copied)).get(1).content().path("spellcasting").get(0);
        assertEquals("{@spell light}", content.path("will").get(0).asText());
        assertEquals("{@spell detect magic}", content.path("constant").get(0).asText());
        assertEquals("{@spell alarm}", content.path("ritual").get(0).asText());
        assertTrue(content.path("daily").path("1e").isEmpty());
    }

    @Test
    void loadsAndResolvesCompleteMonsterCorpus() throws Exception {
        List<MonsterSourceRecord> input = new ArrayList<>();
        Path directory = Path.of("src/main/resources/data/monsters");
        List<Path> sourceFiles;
        try (var files = Files.list(directory)) {
            sourceFiles = files.filter(p -> p.getFileName().toString().startsWith("bestiary-") && p.toString().endsWith(".json")).sorted().toList();
            for (Path file : sourceFiles) {
                JsonNode root = mapper.readTree(file.toFile());
                JsonNode monsters = root.path("monster");
                for (int i = 0; i < monsters.size(); i++) {
                    ObjectNode raw = (ObjectNode) monsters.get(i);
                    MonsterDte dte = mapper.treeToValue(raw, MonsterDte.class);
                    input.add(new MonsterSourceRecord(MonsterIdentity.from(raw.path("name").asText(), raw.path("source").asText()), new MonsterSourceLocation(file.getFileName().toString(), i), dte, raw));
                }
            }
        }
        assertEquals(111, sourceFiles.size());
        assertEquals(4560, input.size());
        var repo = MonsterTemplateRepository.fromResource(mapper);
        assertEquals(66, repo.identities().size());
        var resolver = new DefaultMonsterCopyResolver(mapper, repo);
        var first = resolver.resolveAll(input);
        var second = resolver.resolveAll(input);
        assertEquals(4560, first.size());
        assertEquals(4560, first.stream().map(ResolvedMonster::identity).distinct().count());
        assertEquals(first.stream().map(x -> x.content().toString()).toList(), second.stream().map(x -> x.content().toString()).toList());
        assertTrue(first.stream().noneMatch(x -> x.content().has("_copy")));
        assertEquals(187, first.stream().mapToInt(x -> x.appliedTemplates().size()).sum());
        assertEquals(1144, first.stream().filter(x -> x.copyParent() != null).count());
        Set<String> modes = new TreeSet<>();
        first.forEach(x -> modes.addAll(x.provenance().appliedModifierModes()));
        assertEquals(Set.of("replaceTxt", "replaceArr", "appendArr", "removeArr", "prependArr", "addSpells", "replaceSpells", "addSkills", "remove", "appendIfNotExistsArr", "insertArr", "removeSpells", "setProp", "addSenses", "maxSize", "prefixSuffixStringProp", "scalarAddDc", "scalarAddHit", "scalarAddProp", "scalarMultProp", "scalarMultXp"), modes);
        assertTrue(input.stream().filter(x -> x.identity().equals(MonsterIdentity.from("Skaab", "PSI"))).findFirst().orElseThrow().original().has("_copy"));
        assertTrue(first.stream().filter(x -> x.identity().equals(MonsterIdentity.from("Skaab", "PSI"))).findFirst().orElseThrow().content().path("_versions").isArray());
        JsonNode hillDwarf = repo.require(MonsterIdentity.from("Hill Dwarf", "PHB"));
        assertTrue(hillDwarf.path("apply").path("_root").path("type").path("tags").get(0).path("prefix").asText().equals("Hill"));
        JsonNode catSkeleton = find(first, "Cat Skeleton", "CoS");
        assertTrue(catSkeleton.has("vulnerable") && catSkeleton.get("vulnerable").isNull());
        assertTrue(catSkeleton.has("languages") && catSkeleton.get("languages").isNull());
        JsonNode knight = find(first, "Knight of the Order", "CoS");
        assertEquals(13, knight.path("ac").get(0).path("ac").asInt());
        assertEquals("Longsword", knight.path("action").get(1).path("name").asText());
        JsonNode zikran = find(first, "Zikran", "CM");
        assertTrue(zikran.path("spellcasting").get(0).path("spells").path("5").path("spells").toString().contains("conjure elemental"));
        JsonNode aarakocra = find(first, "Aarakocra Spelljammer", "LoX");
        assertEquals("humanoid", aarakocra.path("type").path("type").asText());
        JsonNode reduced = find(first, "Reduced-Threat Aboleth", "TftYP");
        assertEquals(67, reduced.path("hp").path("average").asInt());
        assertEquals("L", reduced.path("size").get(0).asText());
        assertEquals(2950, reduced.path("cr").path("xp").asInt());
        assertTrue(reduced.path("hp").path("formula").asText().startsWith("floor(("));
    }

    private JsonNode find(List<ResolvedMonster> monsters, String name, String source) {
        return monsters.stream().filter(x -> x.identity().equals(MonsterIdentity.from(name, source))).findFirst().orElseThrow().content();
    }
}
