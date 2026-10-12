package com.thedndpub.data.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SchemaDiscoveryTests {
    @TempDir
    Path temp;
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void writesFourDeterministicDomainReportsAndSeparatesCollectionsAndDomains() throws Exception {
        Path monsters = Files.createDirectories(temp.resolve("in/monsters/nested"));
        Files.writeString(monsters.resolve("one.json"), "{\"monster\":[{\"name\":\"Goblin\",\"x\":null}],\"legendaryGroup\":[{\"name\":\"Dragon\"}],\"_meta\":{\"sources\":[]}}\n");
        Files.writeString(monsters.resolve("two.json"), "{\"monster\":[{\"name\":\"Ogre\",\"x\":2},{\"name\":\"Troll\"}]}\n");
        Path spells = Files.createDirectories(temp.resolve("in/spells"));
        Files.writeString(spells.resolve("same.json"), "{\"monster\":[{\"name\":\"Unrelated\"}]}\n");

        Path out1 = temp.resolve("out1"), out2 = temp.resolve("out2");
        var result = SchemaDiscovery.scan(temp.resolve("in"), out1);
        SchemaDiscovery.scan(temp.resolve("in"), out2);
        assertEquals(0, result.failures().size());
        JsonNode report = mapper.readTree(out1.resolve("monsters/nested/schema-summary.json").toFile());
        assertEquals(3, report.path("collectionEntityCounts").path("monster").asInt());
        JsonNode monsterCoverage = null;
        for (JsonNode collection : report.path("collections")) {
            if (collection.path("collection").asText().equals("monster")) {
                monsterCoverage = collection;
            }
        }
        assertNotNull(monsterCoverage);
        assertEquals(2, monsterCoverage.path("contributingFileCount").asInt());
        assertEquals(3, monsterCoverage.path("entityCount").asInt());
        assertTrue(monsterCoverage.path("discoveredPropertyPathCount").asInt() >= 2);
        assertTrue(monsterCoverage.path("reportLocations").get(0).path("propertyPathPrefix").asText().equals("monster"));
        JsonNode manifest = mapper.readTree(out1.resolve("coverage-manifest.json").toFile());
        assertEquals(4, manifest.path("totalCollections").asInt());
        assertTrue(manifest.path("scanComplete").asBoolean());
        assertTrue(manifest.path("unaccountedFiles").isEmpty());
        assertEquals(Files.readString(out1.resolve("coverage-manifest.json")),
                Files.readString(out2.resolve("coverage-manifest.json")));
        JsonNode x = property(report, "monster[].x");
        assertEquals(1, x.path("nullCount").asInt());
        assertEquals(1, x.path("missingCount").asInt());
        assertTrue(x.path("classifications").toString().contains("POLYMORPHIC"));
        assertEquals(Files.readString(out1.resolve("monsters/nested/schema-summary.json")),
                Files.readString(out2.resolve("monsters/nested/schema-summary.json")));
        for (String name : new String[]{"schema-summary.json", "schema-conflicts.json", "schema-dynamic-keys.json", "schema-gameplay.json"}) {
            assertTrue(Files.exists(out1.resolve("monsters/nested").resolve(name)));
            assertEquals(Files.readString(out1.resolve("monsters/nested").resolve(name)),
                    Files.readString(out2.resolve("monsters/nested").resolve(name)));
        }
        assertTrue(Files.exists(out1.resolve("spells/schema-summary.json")));
    }

    @Test
    void preservesPolymorphismDynamicKeysRecursiveEntriesAndBoundedExamples() throws Exception {
        Path input = Files.createDirectories(temp.resolve("spells"));
        Files.writeString(input.resolve("sample.json"), """
                {"spell":[
                  {"name":"Alpha","components":{"m":"sand"},"spellcasting":[{"daily":{"1e":["x"],"2e":["y"]}}],"entries":[{"entries":[{"entries":[{"entries":["deep"]}]}]}]},
                  {"name":"Beta","components":{"m":{"text":"rod","consume":true}},"entries":["plain"]},
                  {"name":"Gamma","components":{"m":{"text":"dust","consume":"optional"}}}
                ]}
                """);
        var result = SchemaDiscovery.scan(input, temp.resolve("reports"));
        assertTrue(result.failures().isEmpty());
        Path dir = temp.resolve("reports");
        JsonNode conflicts = mapper.readTree(dir.resolve("schema-conflicts.json").toFile());
        JsonNode m = conflict(conflicts, "spell[].components.m");
        assertTrue(m.path("types").has("string"));
        assertTrue(m.path("types").has("object"));
        assertTrue(m.path("examples").path("object").get(0).path("value").path("text").isTextual());
        assertEquals("Beta", m.path("examples").path("object").get(0).path("entity").asText());
        JsonNode consume = conflict(conflicts, "spell[].components.m.consume");
        assertTrue(consume.path("types").has("boolean"));
        assertTrue(consume.path("types").has("string"));
        JsonNode summary = mapper.readTree(dir.resolve("schema-summary.json").toFile());
        assertNotNull(property(summary, "spell[].entries[].entries[].entries"));
        for (JsonNode row : summary.path("properties")) {
            assertFalse(row.path("path").asText().contains("entries[].entries[].entries[]"));
        }
        JsonNode dynamics = mapper.readTree(dir.resolve("schema-dynamic-keys.json").toFile());
        JsonNode daily = null;
        for (JsonNode candidate : dynamics.path("candidates")) {
            if (candidate.path("parentPath").asText().endsWith("daily")) {
                daily = candidate;
            }
        }
        assertNotNull(daily);
        assertTrue(daily.path("observedKeyNames").toString().contains("1e"));
        assertTrue(daily.path("examples").get(0).path("value").path("1e").isArray());
        JsonNode gameplay = mapper.readTree(dir.resolve("schema-gameplay.json").toFile());
        assertTrue(gameplay.path("properties").toString().contains("GAMEPLAY_DESCRIPTION"));
        assertTrue(gameplay.path("properties").toString().contains("PRESENTATION"));
    }

    @Test
    void freeTextDoesNotExpandSummaryAndExamplesRespectSizeBounds() throws Exception {
        Path input = Files.createDirectories(temp.resolve("longtext"));
        String longText = "Z".repeat(4000);
        Files.writeString(input.resolve("fixture.json"), "{\"spell\":[{\"name\":\"Long\",\"entries\":[\"" + longText + "\"]}]}");
        SchemaDiscovery.scan(input, temp.resolve("reports"));
        String summary = Files.readString(temp.resolve("reports/schema-summary.json"));
        assertFalse(summary.contains("Z".repeat(100)));
        JsonNode entries = property(mapper.readTree(summary), "spell[].entries[]");
        assertEquals(4000, entries.path("maxStringLength").asInt());
        assertFalse(entries.has("valueVariations"));
    }

    @Test
    void reportsMalformedFileWithPathAndKeepsScanningOtherFiles() throws Exception {
        Path input = Files.createDirectories(temp.resolve("bad"));
        Files.writeString(input.resolve("broken.json"), "{\"monster\":[{\"action\":[");
        Files.writeString(input.resolve("good.json"), "{\"monster\":[{\"name\":\"OK\"}]}\n");
        var result = SchemaDiscovery.scan(input, temp.resolve("reports"));
        assertEquals(1, result.failures().size());
        assertEquals("monster[].action", result.failures().get(0).get("path"));
        assertTrue(Files.exists(temp.resolve("reports/schema-summary.json")));
        assertFalse(mapper.readTree(temp.resolve("reports/schema-summary.json").toFile()).path("scanComplete").asBoolean());
    }

    private JsonNode property(JsonNode report, String path) {
        for (JsonNode candidate : report.path("properties")) {
            if (candidate.path("path").asText().equals(path)) {
                return candidate;
            }
        }
        return null;
    }

    private JsonNode conflict(JsonNode report, String path) {
        for (JsonNode candidate : report.path("conflicts")) {
            if (candidate.path("path").asText().equals(path)) {
                return candidate;
            }
        }
        return fail("Missing conflict: " + path);
    }
}
