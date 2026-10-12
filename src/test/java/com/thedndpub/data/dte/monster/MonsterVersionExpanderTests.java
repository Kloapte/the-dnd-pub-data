package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MonsterVersionExpanderTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final MonsterCopyResolver copyResolver = new DefaultMonsterCopyResolver(
            mapper, MonsterTemplateRepository.fromResource(mapper));
    private final MonsterVersionExpander expander = new MonsterVersionExpander(mapper, copyResolver);

    @Test
    void expandsDirectVersionsWithOverridesNullDeletionOrderedModsAndPreservesSources() throws Exception {
        ObjectNode base = (ObjectNode) mapper.readTree("""
                {"name":"Fixture","source":"TST","ac":[{"ac":12}],"hp":{"average":20},"trait":[],
                 "action":[{"name":"Old","entries":["start"]}],"page":3,"hasToken":true,
                 "_versions":[
                   {"name":"Fixture Alpha","source":"TST","hp":null,"ac":[{"ac":15}],
                    "_mod":{"action":[{"mode":"appendArr","items":{"name":"Added","entries":["first"]}},
                                    {"mode":"renameArr","renames":{"rename":"Added","with":"Renamed"}}]}},
                   {"name":"Fixture Template","source":"DMG","_templates":[{"name":"Deep Gnome","source":"DMG"}]}
                 ]}
                """);
        ResolvedMonster input = resolved("Fixture", "TST", base);
        List<ResolvedMonster> result = expander.expandAll(List.of(input));
        assertEquals(3, result.size());
        JsonNode alpha = get(result, "Fixture Alpha", "TST");
        assertEquals(15, alpha.path("ac").get(0).path("ac").asInt());
        assertFalse(alpha.has("hp"));
        assertEquals(3, alpha.path("page").asInt());
        assertFalse(alpha.has("hasToken"));
        assertEquals("Renamed", alpha.path("action").get(1).path("name").asText());
        assertTrue(getResolved(result, "Fixture Alpha", "TST").provenance().appliedModifierModes().containsAll(Set.of("appendArr", "renameArr")));
        assertFalse(getResolved(result, "Fixture", "TST").content().has("_versions"));
        assertEquals(2, getResolved(result, "Fixture", "TST").provenance().sourceVersionDefinitions().size());
        assertFalse(getResolved(result, "Fixture Template", "DMG").appliedTemplates().isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> result.add(input));
        assertTrue(input.content().has("_versions"), "expansion must not mutate the input record");
    }

    @Test
    void expandsAbstractImplementationsAndSubstitutesNestedVariablesBeforeOverrides() throws Exception {
        ObjectNode base = (ObjectNode) mapper.readTree("""
                {"name":"Spirit","source":"TST","trait":[],"action":[],"_versions":[
                 {"_abstract":{"name":"Spirit {{element}}","source":"TST","trait":[{"name":"Resistance","entries":["Resistant to {{element}} damage"]}],"ac":[{"ac":10}]},
                  "_implementations":[
                   {"_variables":{"element":"acid"},"ac":[{"ac":13}]},
                   {"_variables":{"element":"cold"},"name":"Spirit of Frost"}
                  ]}]}
                """);
        var expanded = expander.expandAll(List.of(resolved("Spirit", "TST", base)));
        assertEquals("Spirit acid", get(expanded, "Spirit acid", "TST").path("name").asText());
        assertEquals("Resistant to acid damage", get(expanded, "Spirit acid", "TST").path("trait").get(0).path("entries").get(0).asText());
        assertEquals(13, get(expanded, "Spirit acid", "TST").path("ac").get(0).path("ac").asInt());
        assertEquals("Spirit of Frost", get(expanded, "Spirit of Frost", "TST").path("name").asText());
        assertEquals("Resistant to cold damage", get(expanded, "Spirit of Frost", "TST").path("trait").get(0).path("entries").get(0).asText());
        assertEquals("cold", getResolved(expanded, "Spirit of Frost", "TST").provenance().generatedVersion().variables().get("element"));
    }

    @Test
    void errorsOnUnresolvedAbstractVariablesMalformedDefinitionsAndIdentityCollisions() throws Exception {
        ObjectNode unresolved = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","_versions":[{"_abstract":{"name":"{{x}}","source":"TST"},"_implementations":[{"_variables":{}}]}]}
                """);
        assertTrue(assertThrows(MonsterResolutionException.class, () -> expander.expandAll(List.of(resolved("Base", "TST", unresolved))))
                .getMessage().contains("unresolved placeholder"));
        ObjectNode malformed = (ObjectNode) mapper.readTree("{\"name\":\"Base\",\"source\":\"TST\",\"_versions\":[4]}");
        assertTrue(assertThrows(MonsterResolutionException.class, () -> expander.expandAll(List.of(resolved("Base", "TST", malformed))))
                .getMessage().contains("must be an object"));
        ObjectNode malformedVariables = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","_versions":[{"_abstract":{"name":"{{x}}","source":"TST"},"_implementations":[{"_variables":{"x":2}}]}]}
                """);
        assertTrue(assertThrows(MonsterResolutionException.class, () -> expander.expandAll(List.of(resolved("Base", "TST", malformedVariables))))
                .getMessage().contains("must be a string"));
        ObjectNode missingIdentity = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","_versions":[{"source":"TST"}]}
                """);
        assertTrue(assertThrows(MonsterResolutionException.class, () -> expander.expandAll(List.of(resolved("Base", "TST", missingIdentity))))
                .getMessage().contains("name must be"));
        ObjectNode collision = (ObjectNode) mapper.readTree("""
                {"name":"Base","source":"TST","_versions":[{"name":"Already There","source":"TST"}]}
                """);
        ResolvedMonster existing = resolved("Already There", "TST", mapper.createObjectNode().put("name", "Already There").put("source", "TST"));
        assertTrue(assertThrows(MonsterResolutionException.class, () -> expander.expandAll(List.of(resolved("Base", "TST", collision), existing)))
                .getMessage().contains("collides"));
    }

    @Test
    void expandsFullMonsterCorpusTo4851AndPreservesRepresentativeVersionBehavior() throws Exception {
        List<MonsterSourceRecord> source = loadCorpus();
        assertEquals(4560, source.size());
        var resolvedOriginals = copyResolver.resolveAll(source);
        assertEquals(4560, resolvedOriginals.size());
        var expanded = expander.expandAll(resolvedOriginals);
        assertEquals(4851, expanded.size());
        assertEquals(4560, expanded.stream().filter(m -> m.provenance().generatedVersion() == null).count());
        assertEquals(291, expanded.stream().filter(m -> m.provenance().generatedVersion() != null).count());
        assertEquals(85, expanded.stream().filter(m -> m.provenance().generatedVersion() == null && !m.provenance().sourceVersionDefinitions().isEmpty()).count());
        assertEquals(287, expanded.stream().filter(m -> m.provenance().generatedVersion() == null)
                .mapToInt(m -> m.provenance().sourceVersionDefinitions().size()).sum());
        assertEquals(expanded.size(), expanded.stream().map(ResolvedMonster::identity).distinct().count());
        assertTrue(expanded.stream().noneMatch(m -> m.content().has("_copy") || m.content().has("_versions")));
        assertTrue(expanded.stream().filter(m -> m.provenance().generatedVersion() != null)
                .noneMatch(m -> List.of("_mod", "_templates", "_preserve").stream().anyMatch(m.content()::has)));
        Set<String> versionModes = new TreeSet<>();
        expanded.stream().filter(m -> m.provenance().generatedVersion() != null)
                .forEach(m -> collectModes(m.provenance().generatedVersion().sourceDefinition(), versionModes));
        Set<String> executedModes = new TreeSet<>();
        expanded.stream().filter(m -> m.provenance().generatedVersion() != null)
                .forEach(m -> executedModes.addAll(m.provenance().appliedModifierModes()));
        assertTrue(executedModes.containsAll(versionModes), "version modifier modes must all have executed: " + versionModes);

        List<ResolvedMonster> repeat = expander.expandAll(copyResolver.resolveAll(source));
        assertEquals(expanded.stream().map(m -> m.identity() + "\n" + m.content()).toList(),
                repeat.stream().map(m -> m.identity() + "\n" + m.content()).toList());

        List<ResolvedMonster> skaab = variants(expanded, "Skaab", "PSI");
        assertEquals(5, skaab.size());
        assertTrue(skaab.stream().allMatch(m -> m.content().path("action").isArray()));
        List<ResolvedMonster> battleFamiliars = variants(expanded, "Battle Familiar", "AU");
        assertEquals(3, battleFamiliars.size());
        JsonNode brute = get(expanded, "Battle Familiar (Brute)", "AU");
        assertFalse(brute.path("action").toString().contains("Prowl (Stalker Only)"));
        assertFalse(brute.path("trait").toString().contains("Flyby (Flyer Only)"));
        List<ResolvedMonster> draconic = expanded.stream().filter(m -> m.provenance().generatedVersion() != null
                && m.provenance().generatedVersion().containingMonster().equals(MonsterIdentity.from("Draconic Spirit", "XPHB"))).toList();
        assertEquals(5, draconic.size());
        assertTrue(draconic.stream().anyMatch(m -> m.content().toString().contains("{@damage 2d6} Acid damage")));
        assertEquals(5, draconic.stream().map(m -> m.provenance().generatedVersion().variables().get("resist")).distinct().count());
    }

    private List<MonsterSourceRecord> loadCorpus() throws Exception {
        List<MonsterSourceRecord> records = new ArrayList<>();
        Path directory = Path.of("src/main/resources/data/monsters");
        try (var files = Files.list(directory)) {
            for (Path file : files.filter(p -> p.getFileName().toString().startsWith("bestiary-") && p.toString().endsWith(".json")).sorted().toList()) {
                JsonNode monsters = mapper.readTree(file.toFile()).path("monster");
                for (int i = 0; i < monsters.size(); i++) {
                    ObjectNode raw = (ObjectNode) monsters.get(i);
                    MonsterIdentity identity = MonsterIdentity.from(raw.path("name").asText(), raw.path("source").asText());
                    records.add(new MonsterSourceRecord(identity, new MonsterSourceLocation(file.getFileName().toString(), i),
                            mapper.treeToValue(raw, MonsterDte.class), raw));
                }
            }
        }
        return records;
    }

    private ResolvedMonster resolved(String name, String source, ObjectNode content) {
        return new ResolvedMonster(MonsterIdentity.from(name, source), new MonsterSourceLocation("fixture.json", 0), null,
                List.of(), content, new ResolutionProvenance("test", "test", null, null, false, Set.of()));
    }

    private JsonNode get(List<ResolvedMonster> monsters, String name, String source) {
        return getResolved(monsters, name, source).content();
    }

    private ResolvedMonster getResolved(List<ResolvedMonster> monsters, String name, String source) {
        return monsters.stream().filter(m -> m.identity().equals(MonsterIdentity.from(name, source))).findFirst().orElseThrow();
    }

    private List<ResolvedMonster> variants(List<ResolvedMonster> monsters, String name, String source) {
        return monsters.stream().filter(m -> m.provenance().generatedVersion() != null
                && m.provenance().generatedVersion().containingMonster().equals(MonsterIdentity.from(name, source))).toList();
    }

    private void collectModes(JsonNode node, Set<String> modes) {
        if (node.isObject()) {
            JsonNode mode = node.get("mode");
            if (mode != null && mode.isTextual()) {
                modes.add(mode.asText());
            }
            node.elements().forEachRemaining(child -> collectModes(child, modes));
        } else if (node.isArray()) {
            node.elements().forEachRemaining(child -> collectModes(child, modes));
        }
    }
}
