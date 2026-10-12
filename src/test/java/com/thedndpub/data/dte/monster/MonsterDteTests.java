package com.thedndpub.data.dte.monster;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonsterDteTests {
    private final ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    void deserializesEveryMonsterFromDiscoveredBestiaryFiles() throws Exception {
        Path directory = Path.of("src/main/resources/data/monsters");
        List<Path> files;
        try (var walk = Files.list(directory)) {
            files = walk.filter(p -> p.getFileName().toString().startsWith("bestiary-") && p.toString().endsWith(".json")).sorted().toList();
        }
        int successes = 0;
        List<String> failures = new ArrayList<>();
        for (Path file : files) {
            try (var parser = mapper.createParser(file.toFile())) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    assertEquals(JsonToken.FIELD_NAME, parser.currentToken());
                    String collection = parser.currentName();
                    JsonToken token = parser.nextToken();
                    if (!collection.equals("monster")) {
                        parser.skipChildren();
                        continue;
                    }
                    assertEquals(JsonToken.START_ARRAY, token);
                    int index = 0;
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        TokenBuffer buffer = new TokenBuffer(parser);
                        buffer.copyCurrentStructure(parser);
                        String name = entityName(buffer);
                        try (var entityParser = buffer.asParser(mapper)) {
                            entityParser.nextToken();
                            MonsterDte monster = mapper.readValue(entityParser, MonsterDte.class);
                            assertNotNull(monster.identity().name());
                            successes++;
                        } catch (Exception e) {
                            failures.add(file.getFileName() + " monster '" + name + "' index " + index + " path " + jsonPath(e) + ": " + e.getMessage());
                        } finally {
                            buffer.close();
                        }
                        index++;
                    }
                }
            }
        }
        System.out.printf("Monster DTE validation: files=%d successes=%d failures=%d%n", files.size(), successes, failures.size());
        assertEquals(111, files.size());
        assertTrue(failures.isEmpty(), () -> String.join("\n", failures));
        assertEquals(4560, successes, "expected 4,560 successfully deserialized monsters");
    }

    @Test
    void mapsPolymorphicStatsDefensesAndSpellcasting() throws Exception {
        MonsterDte monster = mapper.readValue("""
                {"name":"Variants","source":"TST","page":1,"size":["M"],"alignment":["N",{"alignment":["L","G"],"chance":25}],
                 "type":{"type":{"choose":["fey","fiend"]},"tags":["elf",{"tag":"shapechanger","prefix":"(shapechanger)","prefixHidden":true}]},
                 "ac":[15,{"ac":18,"from":["natural armor"],"condition":"while warded","braces":true}],
                 "hp":{"average":45,"formula":"6d8 + 18"},"speed":{"walk":30,"fly":{"number":50,"condition":"(hover)"},"canHover":true},
                 "cr":{"cr":"3","xp":700,"lair":"4"},"resist":["fire",{"resist":["cold"],"cond":true,"note":"from spells"}],
                 "spellcasting":[{"name":"Casting","type":"spellcasting","ability":"int","headerEntries":["Save {@dc 14}"],
                    "will":["{@spell light}",{"entry":"{@spell wish}","hidden":true}],
                    "spells":{"0":{"spells":["{@spell mage hand}"]},"1":{"slots":2,"lower":1,"spells":["{@spell shield}"]}},
                    "daily":{"1e":["{@spell fireball}"]}}],"action":[{"name":"Strike","entries":["{@damage 1d8}"]}],
                 "_versions":[{"name":"Variant","source":"TST","_mod":{"trait":[{"mode":"removeArr","names":["Old trait"]}]},"ac":[17]}]}
                """, MonsterDte.class);
        assertEquals("Variants", monster.identity().name());
        assertInstanceOf(MonsterArmorClassValueDte.class, monster.statistics().ac().get(0));
        assertEquals(18, assertInstanceOf(MonsterArmorClassDetailsDte.class, monster.statistics().ac().get(1)).ac());
        assertEquals("(hover)", assertInstanceOf(MonsterSpeedDetailsDte.class, monster.statistics().speed().fly()).condition());
        assertEquals(List.of("L", "G"), monster.identity().alignment().get(1).codes());
        MonsterDefenseDetailsDte conditional = assertInstanceOf(MonsterDefenseDetailsDte.class, monster.defenses().resist().get(1));
        assertTrue(conditional.cond());
        assertEquals("{@spell wish}", assertInstanceOf(MonsterSpellDetailsDte.class, monster.features().spellcasting().get(0).will().get(1)).entry());
        assertEquals(List.of("{@spell fireball}"), monster.features().spellcasting().get(0).daily().get("1e").stream().map(MonsterSpellTextDte.class::cast).map(MonsterSpellTextDte::text).toList());
        assertEquals(2, monster.features().spellcasting().get(0).spells().get("1").slots());
        MonsterCopyModifierManyDte modifier = assertInstanceOf(MonsterCopyModifierManyDte.class, monster.versions().get(0).mod().changes().get("trait"));
        assertEquals("Old trait", assertInstanceOf(MonsterNameListDte.class, modifier.modifiers().get(0).names()).values().get(0));
    }

    @Test
    void rejectsUnknownMonsterProperties() {
        var error = assertThrows(Exception.class, () -> mapper.readValue("{\"name\":\"Bad\",\"source\":\"TST\",\"unmappedRule\":true}", MonsterDte.class));
        assertTrue(error.getMessage().contains("unmappedRule"), error::getMessage);
    }

    @Test
    void preservesTypedCopyInstructionsAndReferences() throws Exception {
        MonsterDte monster = mapper.readValue("""
                {"name":"Copied","source":"TST","_copy":{"name":"Base","source":"MM",
                 "_templates":[{"name":"Template","source":"TST"}],"_mod":{"action":[{"mode":"replaceArr","replace":"Bite",
                 "items":{"name":"Bite","entries":["{@damage 1d6}"]}}]}}}
                """, MonsterDte.class);
        assertEquals("Base", monster.copy().name());
        assertEquals("TST", monster.copy().templates().get(0).source());
        MonsterCopyModifierManyDte actions = assertInstanceOf(MonsterCopyModifierManyDte.class, monster.copy().mod().changes().get("action"));
        MonsterCopyFeaturePayloadDte payload = assertInstanceOf(MonsterCopyFeaturePayloadDte.class, actions.modifiers().get(0).items());
        var content = assertInstanceOf(SharedMonsterEntryDte.class, payload.feature().entries().get(0)).entry();
        assertEquals("{@damage 1d6}", assertInstanceOf(com.thedndpub.data.dte.common.entry.TextEntryDte.class, content).text());
    }

    @Test
    void preservesDynamicModifierKeysSpellReplacementsAndNumericSkillOverrides() throws Exception {
        MonsterDte monster = mapper.readValue("""
                {"name":"Modified","source":"TST","_copy":{"name":"Base","source":"MM","_mod":{
                 "*":[{"mode":"replaceTxt","replace":"base","with":"variant","flags":"i"}],
                 "_":[{"mode":"addSkills","skills":{"investigation":2}},{"mode":"replaceSpells","spells":{"7":[{"replace":"{@spell teleport}","with":"{@spell plane shift}"}]}}]}}}
                """, MonsterDte.class);
        assertTrue(monster.copy().mod().changes().containsKey("*"));
        MonsterCopyModifierManyDte general = assertInstanceOf(MonsterCopyModifierManyDte.class, monster.copy().mod().changes().get("_"));
        assertEquals(2, assertInstanceOf(MonsterSkillNumericDte.class, general.modifiers().get(0).skills().get("investigation")).value());
        var spellPatches = assertInstanceOf(MonsterSpellLevelModifierListDte.class, general.modifiers().get(1).spells().get("7"));
        assertEquals("{@spell plane shift}", spellPatches.values().get(0).with());
    }

    private String entityName(TokenBuffer b) throws IOException {
        try (var p = b.asParser(mapper)) {
            if (p.nextToken() != JsonToken.START_OBJECT) {
                return "<invalid>";
            }
            while (p.nextToken() != JsonToken.END_OBJECT) {
                String f = p.currentName();
                JsonToken t = p.nextToken();
                if (f.equals("name") && t == JsonToken.VALUE_STRING) {
                    return p.getText();
                }
                p.skipChildren();
            }
        }
        return "<missing-name>";
    }

    private static String jsonPath(Exception e) {
        if (e instanceof com.fasterxml.jackson.databind.JsonMappingException x) {
            return x.getPath().stream().map(r -> r.getFieldName() != null ? "." + r.getFieldName() : "[" + r.getIndex() + "]").reduce("$", String::concat);
        }
        return "$";
    }
}
